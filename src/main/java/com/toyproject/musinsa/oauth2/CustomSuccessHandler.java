package com.toyproject.musinsa.oauth2;


import com.toyproject.musinsa.dto.user.oauth2.CustomOAuth2User;
import com.toyproject.musinsa.service.jwt.JWTService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


// 하이퍼 링크로 backend API Get 요청시 쿠키 방식으로 JWT 전달.
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTService jwtService;


    // OAuth2 login 성공시
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        // OAuth2 User
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String username = customOAuth2User.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        String role = authority.getAuthority();

        //@AuthenticationPrincipal 에서 userId 사용할 수 있도록. customUserDetails 에 추가.
        long userId = customOAuth2User.getUserId();

        // 토큰 생성

        // jwt 토큰에 비밀번호 등 절대 담지 말기. - 외부에서 볼 수 있음.
        String access = jwtService.createAccessToken("access", username, role,userId);
        String refresh = jwtService.createRefreshToken("refresh", username, role); //24시간

        // Access Header
        response.setHeader("access", access);
        // Refresh Cookie
        response.addCookie(createCookie("refresh", refresh));
        response.addCookie(createCookie("OAuth2","auth"));
        response.setStatus(HttpStatus.OK.value());

        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value){
        Cookie cookie = new Cookie(key, value);

        cookie.setPath("/");

        // 자바 스크립트가 쿠키를 가져가지 못하게.
        cookie.setHttpOnly(true);

        return cookie;
    }
}

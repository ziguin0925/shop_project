package com.toyproject.musinsa.global.filter;


import com.toyproject.musinsa.global.util.JWTUtil;
import com.toyproject.musinsa.dto.user.CustomUserDetails;
import com.toyproject.musinsa.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

/*
*   스프링 시큐리티 filter chain에 요청에 담긴 JWT를 검증하기 위한 커스텀 필터.
*   해당 필터를 통해 요청 헤터 Authorization키에 존재하는 JWT를 검증하고 강제로
*   SecurityContextHolder에 세션을 생성한다.
*   ( 해당 세션은 Stateless상태이므로 하나의 요청마다 생성되며 해당 요청이 끝나면 소멸된다.)
*
*   SecurityConfig에 등록해줘야함.
* */
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/*
        // Cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음.
        String authorization = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName());
            if (cookie.getName().equals("Authorization")) {
                authorization = cookie.getValue();
            }
        }
*/

// 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("access");

// 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }

// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

// 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

// username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);
        long userId = jwtUtil.getUserId(accessToken);

        User userEntity = new User();
        userEntity.setUsername(username);
        userEntity.setRole(role);
        userEntity.setId(userId);

        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);


        filterChain.doFilter(request, response);
    }
}

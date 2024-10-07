package com.toyproject.musinsa.global.filter;


import com.toyproject.musinsa.global.util.JWTUtil;
import com.toyproject.musinsa.entity.RefreshEntity;
import com.toyproject.musinsa.repository.jwt.RefreshRepository;
import com.toyproject.musinsa.dto.user.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

// 로그인 진행시 사용될 필터.("/login")
// jwt 를 이용할 것이기 때문에 Security Filter 이더라고 jwt 패키지에 넣음.
// 로그인 검증을 위한 커스텀 filter
// Secutiry Filter 에서 등록.
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    //UsernamePasswordAuthenticationFilter 는 FormLogin을 disable 시키면 동작을 하지 않기 때문에 커스텀으로 만들어 줘야함.

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    //생성자 활용.
    // Security Config 에서 주입 해줘야 함.
    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 클라이언트 요청에서 username, password 추출.
        // 클라이언트 요청시 form-data 의 Key 값을 username, password로 넣어야함. -> Key를 memberName 등으로 바꾸면 안됨.

        String username = obtainUsername(request);
        String password = obtainPassword(request);

//        //확인 용
//        System.out.println("memberName: " + memberName);
//        System.out.println("password: " + password);

        // Authentication Manager에게 던져주기 위해
        //스프링 시큐리티에서 username과 password를 검증하기 위해 Token에 담아야 함.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,null);

        // additionalAuthenticationChecks 메서드
        // Authentication Manager가 Service를 통해 DB에서 정보를 가져와 검증함.
        // DB에서 정보를 가져오기 위해 Service 작성.
        return authenticationManager.authenticate(authToken);

    }

    // 로그인 성공 시 실행 될 메서드.
    // JWT 발급 해주기.
    // HS256 양방향 대칭키로 연습.(어느정도의 단방향도 가지고 있다고함.) -> 비대칭키로도 해보기.
    // 암호화 키는 application에 저장.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        // Authentication 객체의 getPrincipal() 메서드를 실행하게 되면, UserDetails를 구현한 사용자 객체를 Return
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();


        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> authoritiesIterator = authorities.iterator();
        GrantedAuthority authority = authoritiesIterator.next();

        String role = authority.getAuthority();

        // 토큰 생성
        // jwt 토큰에 비밀번호 등 절대 담지 말기. - 외부에서 볼 수 있음.
        String access = jwtUtil.createJwt("access", username, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L); //24시간


        //Refresh 토큰 저장
        addRefreshEntity(username, refresh, 86400000L);


        // jwt 헤더에 붙인 경우 24.09.15
        // "Bearer " 띄어쓰기 필요.
        // RFC 7235 정의에 따라 인증 헤더 형태를 가져야함.
        // "Authorization : Bearer 인증토큰 string"


        // Accese Header
        response.setHeader("access", access);

        // Refresh Cookie
        response.addCookie(createCookie("refresh", refresh));

        //cookie test
        response.addCookie(createCookie("LoginFilter", "default Login"));

        response.setStatus(HttpStatus.OK.value());
    }

    //로그인 실패 시 실행 될 메서드.
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true); // https
        cookie.setPath("/"); // 프론트 서버 사용시.
        cookie.setHttpOnly(true); // js가 가져가지 못하게.

        return cookie;
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}

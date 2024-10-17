package com.toyproject.musinsa.global.filter;

import com.toyproject.musinsa.service.jwt.JWTService;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilter {

    private final JWTService jwtService;

    public CustomLogoutFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);

    }
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        Cookie[] cookies = request.getCookies();

        // refresh Token 삭제
        try{
            jwtService.deleteRefreshToken(cookies);
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ;
        }




        //Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

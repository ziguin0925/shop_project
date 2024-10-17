package com.toyproject.musinsa.controller.jwt;

import com.toyproject.musinsa.global.util.JWTUtil;
import com.toyproject.musinsa.service.jwt.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@ResponseBody
@RequiredArgsConstructor
public class JWTController {

    private final JWTUtil jwtUtil;
    private final JWTService jwtService;

    // Refresh Rotate
    // Access 토큰이 만료될 때 Refresh 도 같이 교환
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String newRefresh = null;

        //get refresh token
        Cookie[] cookies = request.getCookies();

        // 유효한 Refresh Token인지 확인 및 새로운 Refresh토큰 발급.
        try{
            newRefresh = jwtService.recreateRefreshToken(cookies);
        }catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        String newAccess = null;

        String access= request.getHeader("access");

        //현재 Access Token 변경 감지 및 새로운 Access토큰 발급.
        try{
            newAccess = jwtService.recreateAccessToken(access);
        }catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        //response
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}

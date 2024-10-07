package com.toyproject.musinsa.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Iterator;

@Controller
@ResponseBody
public class MainController {

    @GetMapping("/")
    public String index(){

        // JWTFilter를 통과한 뒤 세션 확인 -> JWTFilter를 통과하는 순간 일시적으로 요청이 끝날 때 까지 Session을 생성.

        // 해당 요청 사용자 이름 확인
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //해당 요청 사용자의 role 확인
        Authentication authentications = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentications.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();

        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        return "Hello World"+ role + username;
    }
}

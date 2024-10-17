package com.toyproject.musinsa.controller;

import com.toyproject.musinsa.global.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    JWTUtil jwtUtil;



    @Cacheable(value = "fruit")
    @GetMapping("/set_redis")
    public String test(@RequestParam String name) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("fruit", name);

        return "setRedis";
    }

    @GetMapping("/get_redis")
    public String test2(@RequestParam String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();


        return ops.get("fruit");
    }

    @GetMapping("/sessiontest")
    public String test3(HttpSession session, @RequestParam String name) {
        System.out.println(name);
        session.setAttribute("name", name);
        return "sessiontest";
    }

    @GetMapping("/session_get")
    public String test4(HttpSession session) {
        return (String)session.getAttribute("name");
    }


    @GetMapping("/jwttest")
    public String test5(HttpServletRequest request) {
        String access = request.getHeader("access");

        return jwtUtil.getCategory(access);

    }



}

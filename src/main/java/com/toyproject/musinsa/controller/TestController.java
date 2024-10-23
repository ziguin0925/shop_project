package com.toyproject.musinsa.controller;

import com.toyproject.musinsa.global.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
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



    @CachePut(cacheNames = "fruit", key = "#name")
    @GetMapping("/set_redis")
    public String test(@RequestParam String name) {

        return "aaaa";
    }

    @GetMapping("/get_redis")
    public String test2(@RequestParam String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();


        return ops.get("fruit");
    }


}

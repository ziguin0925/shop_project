package com.toyproject.musinsa.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//프론트 엔드 서버의 요청을 받기 위한 Cors 허용.
@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .exposedHeaders("Set-Cookie") // 노출할 헤더 값.
                .allowedOrigins("http://localhost:3000"); // 기본 리액트 포트 번호
    }

}

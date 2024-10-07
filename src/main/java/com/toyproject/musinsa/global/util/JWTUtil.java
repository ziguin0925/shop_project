package com.toyproject.musinsa.global.util;


import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

//0.12.3 으로 함. 0.11.5 와 메서드 구현 방식이 다름.
//JWTUtil 을 LoginFilter 에 주입.
@Component
public class JWTUtil {

    // key저장할 객체 변수.
    private SecretKey secretKey;

    // key 객체 타입으로 저장.
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        // application.yml의 secret문자열을 SecretKey인 객체 변수로 암호화 함.
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

    }

    // JWT토큰을 전달 받아서 내부 데이터 확인해서 해당 데이터를 꺼냄.
    // Payload 에서 받아옴.

    public String getUsername(String token) {

        // verifyWith - 검증(우리 서버에서 발급 된게 맞는지, 우리쪽의 암호화 키랑 맞는지)
        // 클레임을 확인하고 payload 부분에서 값을 가져옴.
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);

    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }


    //JWT토큰 생성
    public String createJwt(String category, String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("username", username) // 특정한 Key에 대한 데이터 삽입.
                .claim("role", role)
                .claim("category", category)
                .issuedAt(new Date(System.currentTimeMillis())) // 언제 발행 되었는지.
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 기간.
                .signWith(secretKey) // SecretKey를 통해 암호화.
                .compact(); // 해당 토큰 compact
    }



}

package com.toyproject.musinsa.global.util;


import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

//0.12.3 으로 함. 0.11.5 와 메서드 구현 방식이 다름.
//JWTUtil 을 LoginFilter 에 주입.
@Component
public class JWTUtil {

    // 보안 강화를 위해 Refresh, Access 토큰의 secret key를 따로 둠.
    // key저장할 객체 변수.
    private final SecretKey refreshSecretKey;

    private final SecretKey accessSecretKey;

    private final HashMap<String, SecretKey> tokenKey = new HashMap<>();

    private final long accessExpirationTime;

    private final long refreshExpirationTime;


    // key 객체 타입으로 저장.
    public JWTUtil(
            @Value("${spring.jwt.refresh-secret}") String SECRET1,
            @Value("${spring.jwt.access-secret}") String SECRET2,
            @Value("${spring.jwt.token.access-expiration-time}") long accessExpirationTime,
            @Value("${spring.jwt.token.refresh-expiration-time}") long refreshExpirationTime
    ) {

        // application.yml의 secret문자열을 SecretKey인 객체 변수로 암호화 함.
        this.refreshSecretKey = new SecretKeySpec(SECRET1.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessSecretKey = new SecretKeySpec(SECRET2.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;

        tokenKey.put("refresh", refreshSecretKey);
        tokenKey.put("access", accessSecretKey);

    }
    // 현재 verifyToken으로 데이터 확인할 때 마다 계속 try-catch로 Refresh인지 Access인지 확인하는중 -> 개선 필요.
    // JWT토큰을 전달 받아서 내부 데이터 확인해서 Payload 에서 해당 데이터를 꺼냄.
    public String getUsername(String token) {

        SecretKey secretKey = verifyToken(token);

        // verifyWith - 검증(우리 서버에서 발급 된게 맞는지, 우리쪽의 암호화 키랑 맞는지)
        // 클레임을 확인하고 payload 부분에서 값을 가져옴.
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);

    }

    public String getRole(String token) {

        SecretKey secretKey = verifyToken(token);

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        SecretKey secretKey = verifyToken(token);

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String getCategory(String token) {

//        return Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
        try {
            // Refresh인지 확인
            Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(token);
        }catch (RuntimeException e1){
            try {
                System.out.println(e1.getClass());
                System.out.println(e1.getMessage());
                //Access인지 확인.
                Jwts.parser().verifyWith(accessSecretKey).build().parseSignedClaims(token);
                return "access";
            }catch (RuntimeException e2){
                System.out.println(e2.getClass());
                System.out.println(e2.getMessage());
                throw new RuntimeException("invalid token category");
            }
        }
        return "refresh";
    }

    private SecretKey verifyToken(String token) {
        return tokenKey.get(getCategory(token));
    }


    //JWT토큰 생성
    public String createRefreshJwt(String category, String username, String role) {

        return Jwts.builder()
                .claim("username", username) // 특정한 Key에 대한 데이터 삽입.
                .claim("role", role)
                .claim("category", category)
                .issuedAt(new Date(System.currentTimeMillis())) // 언제 발행 되었는지.
                .expiration(new Date(System.currentTimeMillis() + accessExpirationTime)) // 만료 기간.
                .signWith(refreshSecretKey) // SecretKey를 통해 암호화.
                .compact(); // 해당 토큰 compact
    }

    public String createAccessJwt(String category, String username, String role) {

        return Jwts.builder()
                .claim("username", username) // 특정한 Key에 대한 데이터 삽입.
                .claim("role", role)
                .claim("category", category)
                .issuedAt(new Date(System.currentTimeMillis())) // 언제 발행 되었는지.
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationTime)) // 만료 기간.
                .signWith(accessSecretKey) // SecretKey를 통해 암호화.
                .compact(); // 해당 토큰 compact
    }



}

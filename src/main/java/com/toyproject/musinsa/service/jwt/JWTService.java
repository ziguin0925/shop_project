package com.toyproject.musinsa.service.jwt;

import com.toyproject.musinsa.global.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JWTService {

    private final JWTUtil jwtUtil;

    private final RedisTemplate<String,String> redisTemplate;


    public String createRefreshToken(String category, String username, String role) {

        String refresh = jwtUtil.createRefreshJwt(category, username, role);

        addRefreshEntity(username, refresh);

        return refresh;
    }

    public String recreateRefreshToken(Cookie[] cookies) throws RuntimeException {

        String refresh = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

//            response status code
//            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
            throw new RuntimeException("refresh token null");
        }

        //expired check - AT와 RT가 동시에 유효기간이 지났을 경우 재로그인 시도하도록 예외 던져주기.
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

//            response status code
//            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
            throw new RuntimeException("invalid refresh token");
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

//            response status code
//            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
            throw new RuntimeException("invalid refresh token");
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //Redis에 저장되어 있는지 확인
        Boolean isExist = redisTemplate.hasKey(username);
        if (Boolean.FALSE.equals(isExist)) {

//            response body
//           return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
            throw new RuntimeException("invalid refresh token");
        }


        //make new JWT
        String newRefresh = jwtUtil.createRefreshJwt("refresh", username, role); //24시간

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        addRefreshEntity(username, newRefresh);

        return newRefresh;
    }

    public String createAccessToken(String category, String username, String role) {
        String access = jwtUtil.createAccessJwt(category, username, role);

        return access;
    }


    public String recreateAccessToken(String access) {

        //Access Token에는 더 많은 정보가 들어갈 수 있으므로 Refresh Token과 따로 둠.
        String username = jwtUtil.getUsername(access);
        String role = jwtUtil.getRole(access);

        return jwtUtil.createAccessJwt("access", username, role);
    }


    public void deleteRefreshToken(Cookie[] cookies) {
        String refresh=null;

        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        //refresh null check
        if (refresh == null) {
            throw new RuntimeException("refresh token null");
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) { // 이부분 굳이 할 필요가 있을까. 어짜피 CustomFilter에서 잡아주는데
            throw new RuntimeException("invalid refresh token");
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            //response status code
            throw new RuntimeException("token is not refresh token");
        }
        String username = jwtUtil.getUsername(refresh);

        //DB에 저장되어 있는지 확인
        Boolean isExist = redisTemplate.hasKey(username);
        if (Boolean.FALSE.equals(isExist)) {
            //response status code
            throw new RuntimeException("refresh token is not exist in DB");
        }

        //로그아웃 진행
        // refresh토큰 redis에서 제거.
        redisTemplate.delete(username);
    }


    private void addRefreshEntity(String username, String refresh) {


/*
// DB 대신 Redis사용으로 함.
        long dbExpiredTime = 86400000L;

        Date date = new Date(System.currentTimeMillis() + dbExpiredTime);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());
*/

        // refresh Token은 redis에 16분 동안 상주하도록 하기 - AT 만료기간 15분
        ValueOperations<String,String> ops =redisTemplate.opsForValue();

        //set시 expire 적용시키는 법
        ops.set(username, refresh,30, TimeUnit.MINUTES);

        // set 하고 나서 expire적용시키기.
//        redisTemplate.expire(username,30, TimeUnit.MINUTES);
    }
}

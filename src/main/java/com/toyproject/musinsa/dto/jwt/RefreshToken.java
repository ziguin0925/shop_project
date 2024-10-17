package com.toyproject.musinsa.dto.jwt;


import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
//@RedisHash(value ="refresh_token") // @RedisHash - 설정한 값을 Redis의 key 값 prefix로 사용한다.
public class RefreshToken {

    //@Id - 키(key) 값이 되며, refresh_token:{id} 위치에 auto-increment 된다.
    @Id
    private String authId;

    // @Indexed - 값으로 검색을 할 시에 추가한다.
    @Indexed
    private String token;

    private String role;

    // @TimeToLive - 만료시간을 설정(초(second) 단위)
    @TimeToLive
    private Long ttl;

    public RefreshToken update(String token, long ttl){
        this.token = token;
        this.ttl = ttl;
        return this;
    }
}

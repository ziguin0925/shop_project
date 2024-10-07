package com.toyproject.musinsa.dto.user.oauth2;

public interface OAuth2Response {


    // 제공자 (naver, google ...)
    String getProvider();

    // 제공자에서 발급해주는 아이디 (번호)
    String getProvoiderId();

    //사용자 Email
    String getEmail();

    //사용자 실명 (설정 이름)
    String getName();

}

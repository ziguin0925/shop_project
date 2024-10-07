package com.toyproject.musinsa.dto.user.oauth2;


import java.util.Map;
/*

Google Json방식
{
    resultcode=00, message=success, id=123123123, name= 이름
}
*/

public class GoogleResponse implements OAuth2Response {


    private final Map<String, Object> attributes;

    // 구글의 경우 Json 안에 객체로 정보를 받아오지 않음.
    public GoogleResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProvoiderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}

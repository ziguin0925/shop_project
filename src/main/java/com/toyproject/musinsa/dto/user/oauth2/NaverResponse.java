package com.toyproject.musinsa.dto.user.oauth2;


import java.util.Map;


/*
*  Naver Json
*  {
		resultcode=00, message=success, response={id= 111111 , name= 이름}
    }
*
*
* */

public class NaverResponse implements OAuth2Response{


    private final Map<String, Object> attributes;

    public NaverResponse(Map<String, Object> attributes) {
        this.attributes =(Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProvoiderId() {
        return attributes.get("id").toString();
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

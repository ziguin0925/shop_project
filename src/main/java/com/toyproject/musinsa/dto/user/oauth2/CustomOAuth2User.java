package com.toyproject.musinsa.dto.user.oauth2;

import com.toyproject.musinsa.dto.user.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;

    public CustomOAuth2User(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    // 받은 데이터 값을 리턴 -> 현재는 안쓰는 걸로.
    // 구글과, 네이버 등 각 회사가 가지고 있는 데이터 형태가 응답에 따라서 다르기 때문에 획일화가 어려움.
    // 따라서 따로 가져올 수 있도록 구현함.
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    // Role값 Return
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {

                return userDTO.getRole();
            }
        });

        return authorities;
    }

    @Override
    public String getName() {
        return userDTO.getName();
    }

    public String getUsername(){
        return userDTO.getUsername();
    }

    public long getUserId(){
        return userDTO.getUserId();
    }

}

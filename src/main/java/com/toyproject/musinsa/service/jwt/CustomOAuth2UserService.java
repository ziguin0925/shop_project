package com.toyproject.musinsa.service.jwt;


import com.toyproject.musinsa.dto.user.UserDTO;
import com.toyproject.musinsa.dto.user.oauth2.CustomOAuth2User;
import com.toyproject.musinsa.dto.user.oauth2.GoogleResponse;
import com.toyproject.musinsa.dto.user.oauth2.NaverResponse;
import com.toyproject.musinsa.dto.user.oauth2.OAuth2Response;
import com.toyproject.musinsa.entity.User;
import com.toyproject.musinsa.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// 소셜 로그인시.
//Security Config의 OAuth2Login에 등록을 시켜줘야 함.
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService  extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {


        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        // 어느 사이트(naver, google ...)에서 온 값인지
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        // 각각의 소셜 로그인 회사 별로 Json 데이터 방식 구조가 다르기 때문에 전처리 하는 로직.
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        } else{

            return null; // 등록되지 않은 OAuth2 회사.
        }


        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디 값을 만듦.
        // providerId가 유니크 하기 때문에 상관은 없을거 같은데, 혹시나 naver와 google의 providerId가 겹치는 경우 또는 소셜 로그인과 일반 로그인 사용시 유니크하지 못할 경우를 대비하여 안전 장치
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getName();
        Optional<User> userExist = userRepository.findByUsername(username);

        // 해당 유저가 존재하지 않으면.
        if(userExist.isEmpty()){

            //새로운 유저 생성.
            User userEntity = new User();
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setName(oAuth2Response.getName());
            userEntity.setRole("ROLE_USER");

            // DB에 생성.
            userRepository.save(userEntity);


            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(userEntity.getId());
            userDTO.setUsername(userEntity.getUsername());
            userDTO.setName(userEntity.getName());
            userDTO.setRole(userEntity.getRole());

            //OAuth2LoginAuthenticationProvider 에 전달하기 위해 OAuth2User 의 변수타입으로 전달해야함.
            return new CustomOAuth2User(userDTO);

        }else{
//            username의 경우에는 DB에서 조회 후 받아 왔기 때문에 따로 검증 x
//            userExist.get().setUsername(username);
            User userData =userExist.get();

            userData.setEmail(oAuth2Response.getEmail());
            userData.setName(oAuth2Response.getName());

            userRepository.save(userData);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(userData.getUsername());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(userData.getRole());

            return new CustomOAuth2User(userDTO);

        }






    }

}

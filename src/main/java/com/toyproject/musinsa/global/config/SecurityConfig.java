package com.toyproject.musinsa.global.config;

import com.toyproject.musinsa.global.filter.CustomLogoutFilter;
import com.toyproject.musinsa.global.filter.JWTFilter;
import com.toyproject.musinsa.global.filter.LoginFilter;
import com.toyproject.musinsa.global.util.JWTUtil;
import com.toyproject.musinsa.service.jwt.CustomOAuth2UserService;
import com.toyproject.musinsa.oauth2.CustomSuccessHandler;
import com.toyproject.musinsa.service.jwt.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTService jwtService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Login Filter의 AuthenticationManager 를 파라미터로 넘기기위해 Bean으로 설정.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                                CorsConfiguration config = new CorsConfiguration();

                                config.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 프론트엔드 서버.
                                config.setAllowedMethods(Collections.singletonList("*")); // get, post, option등 모두 허용.
                                config.setAllowCredentials(true); //
                                config.setAllowedHeaders(Collections.singletonList("*")); // 허용 헤더
                                config.setMaxAge(3600L); // 허용 시간.


                                config.setExposedHeaders(Collections.singletonList("Authorization")); // 백엔드 서버에서 사용자 클라이언트 쪽으로 Authorization 보내주는거 허용.
                                config.setExposedHeaders(Collections.singletonList("Set-Cookie")); // 쿠키 허용.
                                return config;
                            }
                        }))
        ;

        // JWT는 stateless 상태로 세션을 관리하기 때문에 방어가 많이 필요없음 -> disable.
        http
                .csrf(AbstractHttpConfigurer::disable) // (auth) -> auth.disable(). 과 같음.
        ;


        // http basic 인증 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable)
        ;

        //경로별 인가 작업. -  uri 큰 범위가 아래로 가도록 해야함.
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/login","/join","/reissue","/set_redis","/get_redis","/sessiontest","/session_get","/jwttest").permitAll()
                        .requestMatchers(HttpMethod.GET,"/jwttest").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/my/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                )
        ;

        // login 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable)

        //나중에는 이거로 해서 url 변경도 하기. OAuth와 똑같이 핸들러 만들기. or 유저 부분 통합
//        http
//                .formLogin((formLogin) -> formLogin
//                                .loginProcessingUrl("/login")
//                                .successHandler(customSuccessHandler)
////                              .failureHandler()
//                                .permitAll()
//                )

        // UsernamePasswordAuthenticationFilter를 통해 Login 처리
        ;

        // OAuth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) ->userInfoEndpointConfig
                                .userService(customOAuth2UserService)) // OAuth2 데이터를 가져왔을때 해당 서비스 사용.
                        .successHandler(customSuccessHandler) //CustomHandler 적용 - 쿠키방식으로 JWT 반환
                )
        //OAuth2LoginAuthenticationFilter 를 통해 login 처리
        ;


        // filter 등록
        // At, Before, After
        http
                // debug true 해서 보면 UsernamePasswordAuthenticationFilter 자리에 LoginFilter가 들어감.
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtService), UsernamePasswordAuthenticationFilter.class) //
        ;

        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class) // LoginFilter 앞에다가 JWT필터를 넣어줌.
                //DB에 저장하고 있는 Refresh 토큰 삭제
                //Refresh 토큰 쿠키 null로 변경
                .addFilterBefore(new CustomLogoutFilter(jwtService), LogoutFilter.class);
        ;

        // Session 설정.
        // JWT를 통한 인증/인가를 위해서 세션을 Stateless 상태로 설정하는 것이 중요하다고 함. -> 서버측에서 메모리에 저장 x

//        http
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 중요.
//                )
//        ;

        return http.build();
    }
}
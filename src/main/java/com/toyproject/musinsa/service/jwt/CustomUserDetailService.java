package com.toyproject.musinsa.service.jwt;

import com.toyproject.musinsa.dto.user.CustomUserDetails;
import com.toyproject.musinsa.entity.User;
import com.toyproject.musinsa.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// 일반 로그인 시 활용.
// Authentication Manager가 필요로 하는 Service - 회원 정보 조회 후 인증되면 JWT 반환 해주기 위해.
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    // 파라미터 Key를 username으로 받음. -> 클라이언트에서 memberName 전송 하면 맵핑 안됨.
    // 비밀번호도 검증해줌. DaoAuthenticationProvider
    // 사용자 정보를 DB에서 조회하고 반환.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        Optional<User> userData = userRepository.findByUsername(username);
        if (userData.isEmpty()) throw new UsernameNotFoundException("존재하지 않는 username 입니다.");

        // CustomMemberDetails는 데이터를 넘겨주는  DTO에 해당하는거임.
        return userData.map(CustomUserDetails::new).orElse(null);

    }
}

package com.toyproject.musinsa.service.user;


import com.toyproject.musinsa.dto.user.JoinDTO;
import com.toyproject.musinsa.entity.User;
import com.toyproject.musinsa.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    //회원 가입.
    public void joinProcess(JoinDTO joinDTO){
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean isExist = userRepository.existsByUsername(username);

        if(isExist){
            return ;
        }

        User user = new User();

        System.out.println(username);

        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password)); // 무조건 암호화를 진행하여 넣어야함.
        user.setRole("ROLE_ADMIN"); // 앞에 ROLE 을 적고 뒤에 권한을 줌.

        userRepository.save(user);

    }


}

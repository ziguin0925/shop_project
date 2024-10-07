package com.toyproject.musinsa.controller.user;

import com.toyproject.musinsa.dto.user.JoinDTO;
import com.toyproject.musinsa.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody //API로 동작 하도록 - RestController와 동일.
@RequiredArgsConstructor
public class UserController {

    // Autowired, RequiredArgsConstructor 보다 생성자 주입 방식을 권고하고 있다고함.
    private final UserService memberService;



    // 회원가입
    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO) {

        memberService.joinProcess(joinDTO);
        return "ok";

    }


    @GetMapping("/my")
    public String myAPI(){

        return "my";
    }
}

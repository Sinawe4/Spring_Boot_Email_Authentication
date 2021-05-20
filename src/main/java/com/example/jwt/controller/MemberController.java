package com.example.jwt.controller;

import com.example.jwt.advice.exception.UserLoginFailedException;
import com.example.jwt.domain.Member;
import com.example.jwt.domain.Response;
import com.example.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.donggeun.springSecurity.model.Response;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MemberController {

    private final AuthService authService;

    @PostMapping("/signup")
    public Response signUpUser(@RequestBody Member member){
        Response response = new Response();

        try {
            authService.signUpUser(member);
            response.setResponse("success");
            response.setMessage("회원가입을 성공적으로 완료했습니다.");
        }
        catch (Exception e){
            throw new UserLoginFailedException();
        }
        return response;
    }


}

package com.example.jwt.controller;

import com.example.jwt.domain.response.CommonResult;
import com.example.jwt.domain.response.ResponseService;
import com.example.jwt.domain.response.SingleResult;
import com.example.jwt.dto.*;
import com.example.jwt.util.JwtUtil;
import com.example.jwt.util.RedisUtil;
import com.example.jwt.domain.Member;
import com.example.jwt.domain.Response;
import com.example.jwt.service.AuthService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MemberController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final ResponseService responseService;

    @PostMapping("/signup")
    public CommonResult signUpUser(@RequestBody MemberDto memberDto) throws Exception{
        authService.signUpUser(memberDto);
        return responseService.getSuccessResult();
    }

    @PostMapping("/login")
    public SingleResult<Map<String,String >> login (@RequestBody MemberSigninDto memberSigninDto) throws Exception {
        return responseService.getSingleResult(authService.loginUser(memberSigninDto.getUsername(), memberSigninDto.getPassword()));
    }

    @PostMapping("/verify")
    public CommonResult verify(@RequestBody RequestVerifyEmailDto requestVerifyEmailDto, HttpServletRequest req,HttpServletResponse res) throws NotFoundException {
        authService.sendVerificationMail(authService.findByUsername(requestVerifyEmailDto.getUsername()));
        return responseService.getSuccessResult();
    }

    @GetMapping("/verify/key")
    public CommonResult getVerify(@RequestBody String key){
        authService.verifyEmail(key);
        return responseService.getSuccessResult();
    }

    @PostMapping("/password")
    public CommonResult requestChangePassword(@RequestBody RequestChangePasswordEmailDto requestChangePasswordEmailDto) throws NoSuchFieldException {
        Member member = authService.findByUsername(requestChangePasswordEmailDto.getUsername());
        if (!member.getEmail().equals(requestChangePasswordEmailDto.getEmail())) throw new NoSuchFieldException();
        authService.requestChangePassword(member);
        return responseService.getSuccessResult();
    }

    @GetMapping("/password/key")
    public CommonResult isPasswordKeyValidate(@RequestBody String key) {
        authService.isPasswordKeyValidate(key);
        return responseService.getSuccessResult();
    }

    @PutMapping("/password")
    public CommonResult changePassword(@RequestBody RequestChangePasswordDto requestChangePasswordDto) {
        authService.changePassword(authService.findByUsername(requestChangePasswordDto.getUsername()),requestChangePasswordDto.getPassword());
        return responseService.getSuccessResult();
    }
}

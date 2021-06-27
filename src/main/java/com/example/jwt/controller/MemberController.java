package com.example.jwt.controller;

import com.example.jwt.advice.exception.UserLoginFailedException;
import com.example.jwt.dto.MemberSigninDto;
import com.example.jwt.dto.RequestChangePasswordDto;
import com.example.jwt.dto.RequestChangePasswordEmailDto;
import com.example.jwt.dto.RequestVerifyEmailDto;
import com.example.jwt.util.CookieUtil;
import com.example.jwt.util.JwtUtil;
import com.example.jwt.util.RedisUtil;
import com.example.jwt.domain.Member;
import com.example.jwt.domain.Response;
import com.example.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MemberController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

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

    @PostMapping("/login")
    public Response login (@RequestBody MemberSigninDto memberSigninDto,
                           HttpServletRequest request,
                           HttpServletResponse response){
        try {
            final Member member = authService.loginUser(memberSigninDto.getUsername(), memberSigninDto.getPassword());
            final String token = jwtUtil.generateToken(member);
            final String refreshJwt = jwtUtil.generateRefreshToken(member);
            Cookie accessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, token);
            Cookie refreshToken = cookieUtil.createCookie(JwtUtil.REFRESH_TOKEN_NAME, refreshJwt);
            redisUtil.setDataExpire(refreshJwt, member.getUsername(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
            response.addCookie(accessToken);
            response.addCookie(refreshToken);
            return new Response("Success", "로그인에 성공했습니다.", token);
        } catch (Exception e) {
            System.out.println(e);
            throw new UserLoginFailedException();
        }
    }
    @PostMapping("/verify")
    public Response verify(@RequestBody RequestVerifyEmailDto requestVerifyEmailDto, HttpServletRequest servletRequest,HttpServletResponse servletResponse){
        Response response;
        try{
            Member member = authService.findByUsername(requestVerifyEmailDto.getUsername());
            authService.sendVerificationMail(member);
            response = new Response("success", "성공적으로 인증메일을 보냈습니다.", null);
        } catch (Exception e) {
            System.out.println(e);
            response = new Response("error", "인증메일을 확인하는데 실패했습니다.", null);
        }
        return response;
    }

    @GetMapping("/verify/key")
    public Response getVerify(@RequestBody String key){
        Response response;
        try {
            authService.verifyEmail(key);
            response = new Response("success", "성공적으로 인증메일을 확인했습니다.", null);
        } catch (Exception e){
            System.out.println(e);
            response = new Response("error", "인증메일을 확인하는데 실패했습니다.", null);
        }
        return response;
    }

    @PostMapping("/password")
    public Response requestChangePassword(@RequestBody RequestChangePasswordEmailDto requestChangePasswordEmailDto) {
        Response response;
        try {
            Member member = authService.findByUsername(requestChangePasswordEmailDto.getUsername());
            if (!member.getEmail().equals(requestChangePasswordEmailDto.getEmail())) throw new NoSuchFieldException("");
            authService.requestChangePassword(member);
            response = new Response("success", "성공적으로 사용자의 비밀번호 변경요청을 수행했습니다.", null);
        } catch (NoSuchFieldException e) {
            response = new Response("error", "사용자 정보를 조회할 수 없습니다.", null);
        } catch (Exception e) {
            response = new Response("error", "비밀번호 변경 요청을 할 수 없습니다.", null);
        }
        return response;
    }

    @GetMapping("/password/key")
    public Response isPasswordKeyValidate(@RequestBody String key) {
        Response response;
        try {
            authService.isPasswordKeyValidate(key);
            response = new Response("success", "정상적인 접근입니다.", null);
        } catch (Exception e) {
            response = new Response("error", "유효하지 않은 key값입니다.", null);
        }
        return response;
    }

    @PutMapping("/password")
    public Response changePassword(@RequestBody RequestChangePasswordDto requestChangePasswordDto) {
        Response response;
        try{
            Member member = authService.findByUsername(requestChangePasswordDto.getUsername());
            authService.changePassword(member,requestChangePasswordDto.getPassword());
            response = new Response("success","성공적으로 사용자의 비밀번호를 변경했습니다.",null);
        }catch(Exception e){
            response = new Response("error","사용자의 비밀번호를 변경할 수 없었습니다.",null);
        }
        return response;

    }
}

package com.example.jwt.controller;

import com.example.jwt.advice.exception.UserLoginFailedException;
import com.example.jwt.dto.MemberSigninDto;
import com.example.jwt.util.CookieUtil;
import com.example.jwt.util.JwtUtil;
import com.example.jwt.util.RedisUtil;
import com.example.jwt.domain.Member;
import com.example.jwt.domain.Response;
import com.example.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            throw new UserLoginFailedException();
        }
    }
}

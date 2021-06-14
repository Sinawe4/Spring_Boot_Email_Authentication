package com.example.jwt.config;

import com.example.jwt.domain.Member;
import com.example.jwt.util.CookieUtil;
import com.example.jwt.util.JwtUtil;
import com.example.jwt.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor

public class JwtRequestFilter extends OncePerRequestFilter {

    final private MyUserDetailsService userDetailsService;

    final private JwtUtil jwtUtil;

    final private CookieUtil cookieUtil;

    final private RedisUtil redisUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final Cookie jwtToken = cookieUtil.getCookie(httpServletRequest,JwtUtil.ACCESS_TOKEN_NAME); //쿠키를 가져와 jwtToken에 넣음

        String username = null;
        String jwt = null;
        String refreshJwt = null;
        String refreshusername = null;

        try{
            if(jwtToken != null){ //가져온 쿠키가 cookieUtil.getCookie를 통해 올바른 값이 왔는지 확인
                jwt = jwtToken.getValue();
                username = jwtUtil.getUsername(jwt); //해당 토큰의 안에 있는 유저이름을 가져온다.
            }
            if(username != null){ //유저 이름을 성공적으로 가져왔을 경우
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)){ //토큰의 만료확인과 토큰속의 Username 과 UserDetails 속의 Username이 일치하는지 확인.
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities()); //인증을 위한 값을 담음
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)); //인증
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // 인증 정보가 일치함으로 context 에 인증정보를 저장하고 통과, filter 외부의 컨트롤러에서도 인증정보를 참조하기에 저장해두어야 한다.
                }
            }
        }catch (ExpiredJwtException e){ //토큰이 만료되었을때
            Cookie refreshToken = cookieUtil.getCookie(httpServletRequest,JwtUtil.REFRESH_TOKEN_NAME); //리프레쉬 토큰이 있나 검사
            if(refreshToken!=null){ //리프레쉬 토큰이 있을때
                refreshJwt = refreshToken.getValue(); //토큰에 리프레쉬토큰의 값을 넣음.
            }
        }catch (Exception e){

        }
        try{
            if(refreshJwt != null){  //리프레쉬 토큰에 값이 있을때
                refreshusername = redisUtil.getData(refreshJwt); //redis를 사용하여 값을 직렬화하여 가져옴

                UserDetails userDetails = userDetailsService.loadUserByUsername(refreshusername);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities()); //인증을 위한 값을 담음
                usernamePasswordAuthenticationToken.setDetails((new WebAuthenticationDetailsSource().buildDetails(httpServletRequest))); //인증
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // 인증 정보가 일치함으로 context 에 인증정보를 저장하고 통과, filter 외부의 컨트롤러에서도 인증정보를 참조하기에 저장해두어야 한다.

                Member member = new Member();
                member.setUsername(refreshusername);
                String newToken = jwtUtil.generateToken(member); //Refresh 토큰속 정보로 AccessToken 생성

                Cookie newAccessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME,newToken); //토큰을 쿠키에 담는다
                httpServletResponse.addCookie(newAccessToken);

            }
        }catch (ExpiredJwtException e){

        }

        filterChain.doFilter(httpServletRequest,httpServletResponse); //필터 작동
    }
}

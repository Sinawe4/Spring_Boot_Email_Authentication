package com.example.jwt.config;

import com.example.jwt.advice.exception.AccessTokenExpiredException;
import com.example.jwt.advice.exception.InvalidTokenException;
import com.example.jwt.advice.exception.UserNotFoundException;
import com.example.jwt.domain.Member;
import com.example.jwt.util.JwtUtil;
import com.example.jwt.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InvalidClassException;

@Slf4j
@Component
@RequiredArgsConstructor

public class JwtRequestFilter extends OncePerRequestFilter {

    final private MyUserDetailsService userDetailsService;

    final private JwtUtil jwtUtil;

    final private RedisUtil redisUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        String accessJwt = req.getHeader("Authorization");
        String refreshJwt = req.getHeader("RefreshToken");

        String username = null;
        String refreshusername = null;

        try{
            if(accessJwt != null){ // 가져온 토큰이 올바른 값이 왔는지 확인
                username = jwtUtil.getUsername(accessJwt); //해당 토큰의 안에 있는 유저이름을 가져온다.
            }
            if(username != null){ //유저 이름을 성공적으로 가져왔을 경우
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                try {
                    if (jwtUtil.validateToken(accessJwt, userDetails)) { //토큰의 만료확인과 토큰속의 Username 과 UserDetails 속의 Username이 일치하는지 확인.
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); //인증을 위한 값을 담음
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req)); //인증
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // 인증 정보가 일치함으로 context 에 인증정보를 저장하고 통과, filter 외부의 컨트롤러에서도 인증정보를 참조하기에 저장해두어야 한다.
                    }
                }catch (NullPointerException e){
                    throw new UserNotFoundException();
                }

            }
        }catch (ExpiredJwtException e){ //토큰이 만료되었을때
            if(refreshJwt!=null){ //리프레쉬 토큰이 있을때
                refreshusername = jwtUtil.getUsername(refreshJwt);
                if (refreshJwt.equals(redisUtil.getData(refreshusername))){
                    String newJwt = jwtUtil.generateToken(refreshusername);
                    res.addHeader("JwtToken",newJwt);
                }else {
                    throw new AccessTokenExpiredException();
                }
            }
        }catch (MalformedJwtException e){
            throw new InvalidTokenException();
        }catch (IllegalArgumentException e){
        }

        filterChain.doFilter(req,res); //필터 작동
    }
}

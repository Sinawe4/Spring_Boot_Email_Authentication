package com.example.jwt.service;

import com.example.jwt.advice.exception.UserNotFoundException;
import com.example.jwt.domain.Salt;
import com.example.jwt.domain.UserRole;
import com.example.jwt.dto.MemberDto;
import com.example.jwt.dto.MemberSigninDto;
import com.example.jwt.util.*;
import com.example.jwt.domain.Member;
import com.example.jwt.repository.MemberRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthServicempl implements AuthService{

    private final MemberRepository memberRepository;
    private final SaltUtil saltUtil;
    private final RedisUtil redisUtil;
    private final EmailService emailService;
    private final KeyUtil keyUtil;
    private final JwtUtil jwtUtil;

    @Override
    public void signUpUser(MemberDto memberDto) {
        if (memberRepository.findByUsername(memberDto.getUsername()) != null){
            throw new U();
        }
        String salt = saltUtil.genSalt();
        System.out.println(salt);
        memberDto.setSalt(new Salt((salt)));
        memberDto.setPassword(saltUtil.encodePassword(salt, memberDto.getPassword()));
        memberRepository.save(memberDto.toEntity());
    }

    @Override
    public Map<String,String> loginUser(String id, String password){
        Member member = memberRepository.findByUsername(id);
        if (member == null) throw new UserNotFoundException();
        String salt = member.getSalt().getSalt();
        password = saltUtil.encodePassword(salt,password);
        if(!member.getPassword().equals(password))
            throw new UserNotFoundException();
        final String accessToken = jwtUtil.generateToken(member.getUsername());
        final String refreshJwt = jwtUtil.generateRefreshToken(member.getUsername());
        redisUtil.setDataExpire(refreshJwt, member.getUsername(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
        Map<String ,String> map = new HashMap<>();
        map.put("username", member.getUsername());
        map.put("acessToken",accessToken);
        map.put("refreshToken", refreshJwt);
        return map;
    }

    @Override
    public void sendVerificationMail(Member member) throws NotFoundException {
        if(member==null) throw new UserNotFoundException();
        String authkey = keyUtil.getKey(6);
        redisUtil.setDataExpire(authkey,member.getUsername(),60 * 30L);
        emailService.sendMail(member.getEmail(),"[Tita] 회원가입 인증 이메일 입니다.","인증번호는 "+authkey);

    }

    @Override
    public void verifyEmail(String key) throws UserNotFoundException {
        String memberId = redisUtil.getData(key);
        Member member = memberRepository.findByUsername(memberId);
        if (member==null) throw new UserNotFoundException();
        modifyUserRole(member,UserRole.ROLE_USER);
        redisUtil.deleteData(key);
    }

    @Override
    public void modifyUserRole(Member member, UserRole userRole) {
        member.setRole(userRole);
        memberRepository.save(member);
    }

    @Override
    public Member findByUsername(String username) throws UserNotFoundException {
        Member member = memberRepository.findByUsername(username);
        if (member==null) throw new UserNotFoundException();
        return member;
    }

    @Override
    public void requestChangePassword(Member member) throws UserNotFoundException{
        String authkey = keyUtil.getKey(6);
        if(member == null) throw new UserNotFoundException("멤버가 조회되지 않음.");
        redisUtil.setDataExpire(authkey,member.getUsername(),60 * 30L);
        emailService.sendMail(member.getEmail(),"[Tita] 사용자 비밀번호 변경 메일입니다.","인증번호는 "+ authkey);
    }

    @Override
    public void isPasswordKeyValidate(String key){
        String memberId = redisUtil.getData(key);
        Member member = memberRepository.findByUsername(memberId);
        if (member==null) throw new UserNotFoundException("멤버가 조회되지않음.");
        modifyUserRole(member,UserRole.ROLE_PASSWORD_CHANGE);
    }

    @Override
    public void changePassword(Member member,String password) throws UserNotFoundException{
        if(member == null) throw new UserNotFoundException("changePassword(),멤버가 조회되지 않음");
        String salt = saltUtil.genSalt();
        member.setSalt(new Salt(salt));
        member.setPassword(saltUtil.encodePassword(salt,password));
        member.setRole(UserRole.ROLE_USER);
        memberRepository.save(member);
    }

}

package com.example.jwt.service;

import com.example.jwt.advice.exception.UserNotFoundException;
import com.example.jwt.domain.Salt;
import com.example.jwt.domain.UserRole;
import com.example.jwt.util.RedisUtil;
import com.example.jwt.util.SaltUtil;
import com.example.jwt.domain.Member;
import com.example.jwt.repository.MemberRepository;
import com.example.jwt.repository.SaltRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServicempl implements AuthService{

    private final MemberRepository memberRepository;
    private final SaltRepository saltRepository;
    private final SaltUtil saltUtil;
    private final RedisUtil redisUtil;
    private final EmailService emailService;

    @Override

    public void signUpUser(Member member) {
        String password = member.getPassword();
        String salt = saltUtil.genSalt();
        System.out.println(salt);
        member.setSalt(new Salt((salt)));
        member.setPassword(saltUtil.encodePassword(salt,password));
        memberRepository.save(member);
    }

    @Override
    public Member loginUser(String id, String password){
        Member member = memberRepository.findByUsername(id);
        if (member == null) throw new UserNotFoundException();
        String salt = member.getSalt().getSalt();
        password = saltUtil.encodePassword(salt,password);
        if(!member.getPassword().equals(password))
            throw new UserNotFoundException();
        return member;
    }

    @Override
    public void sendVerificationMail(Member member) throws NotFoundException {
        String VERIFICATTION_LINK = "http://localhost:8080/user/verify/";
        if(member==null) throw new NotFoundException("멤버가 조회되지 않음");
        UUID uuid = UUID.randomUUID();
        redisUtil.setDataExpire(uuid.toString(),member.getUsername(),60 * 30L);
        emailService.sendMail(member.getEmail(),"[Tita] 회원가입 인증 이메일 입니다.",VERIFICATTION_LINK+uuid.toString());

    }

    @Override
    public void verifyEmail(String key) throws UserNotFoundException {
        String memberId = redisUtil.getData(key);
        Member member = memberRepository.findByUsername(memberId);
        if (member==null) throw new UserNotFoundException("멤버가 조회되지않음.");
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
    public void isPasswordUuidValidate(String key){
        String memberId = redisUtil.getData(key);
        Member member = memberRepository.findByUsername(memberId);
        if (member==null) throw new UserNotFoundException("멤버가 조회되지않음.");
        modifyUserRole(member,UserRole.ROLE_PASSWORD_CHANGE);
    }


    @Override
    public void requestChangePassword(Member member) throws UserNotFoundException{
        String CHANGE_PASSWORD_LINK = "http://localhost:8080/user/password/";
        if(member == null) throw new UserNotFoundException("멤버가 조회되지 않음.");
        String key = REDIS_CHANGE_PASSWORD_PREFIX+UUID.randomUUID();
        redisUtil.setDataExpire(key,member.getUsername(),60 * 30L);
        emailService.sendMail(member.getEmail(),"[Tita] 사용자 비밀번호 변경 메일입니다.",CHANGE_PASSWORD_LINK+key);
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

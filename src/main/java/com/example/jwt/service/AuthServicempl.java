package com.example.jwt.service;

import com.example.jwt.advice.exception.UserNotFoundException;
import com.example.jwt.config.Salt;
import com.example.jwt.config.SaltUtil;
import com.example.jwt.domain.Member;
import com.example.jwt.repository.MemberRepository;
import com.example.jwt.repository.SaltRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServicempl implements AuthService{

    private final MemberRepository memberRepository;
    private final SaltRepository saltRepository;
    private final SaltUtil saltUtil;

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
        String salt = member.getSalt().getSait();
        password = saltUtil.encodePassword(salt,password);
        if(!member.getPassword().equals(password))
            throw new UserNotFoundException();
        return member;
    }
}

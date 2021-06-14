package com.example.jwt.service;

import com.example.jwt.advice.exception.UserNotFoundException;
import com.example.jwt.domain.Salt;
import com.example.jwt.util.SaltUtil;
import com.example.jwt.domain.Member;
import com.example.jwt.repository.MemberRepository;
import com.example.jwt.repository.SaltRepository;
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
        String salt = member.getSalt().getSalt();
        password = saltUtil.encodePassword(salt,password);
        if(!member.getPassword().equals(password))
            throw new UserNotFoundException();
        return member;
    }
}

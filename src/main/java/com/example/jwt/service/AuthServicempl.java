package com.example.jwt.service;

import com.example.jwt.domain.Member;
import com.example.jwt.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServicempl implements AuthService{

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void signUpUser(Member member) {
        String password = member.getPassword();
        String salt = saltUtil.getSalt();
    }

    @Override
    public Member loginUser(String id, String password) {
        return null;
    }
}

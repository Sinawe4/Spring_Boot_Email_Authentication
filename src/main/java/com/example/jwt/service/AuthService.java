package com.example.jwt.service;

import com.example.jwt.domain.Member;

public interface AuthService {
    void signUpUser(Member member);

    Member loginUser(String id, String password) throws Exception;
}

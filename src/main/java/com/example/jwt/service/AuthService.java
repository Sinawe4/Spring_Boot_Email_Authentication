package com.example.jwt.service;

import com.example.jwt.advice.exception.UserNotFoundException;
import com.example.jwt.domain.Member;
import com.example.jwt.domain.UserRole;
import javassist.NotFoundException;

public interface AuthService {
    void signUpUser(Member member);

    Member loginUser(String id, String password) throws Exception;

    void sendVerificationMail(Member member) throws NotFoundException;

    void verifyEmail(String key) throws NotFoundException;

    void modifyUserRole(Member member, UserRole userRole);
    Member findByUsername(String username) throws UserNotFoundException;
}

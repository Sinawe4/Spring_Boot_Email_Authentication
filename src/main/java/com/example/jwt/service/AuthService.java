package com.example.jwt.service;

import com.example.jwt.advice.exception.UserNotFoundException;
import com.example.jwt.domain.Member;
import com.example.jwt.domain.UserRole;
import javassist.NotFoundException;

public interface AuthService {

    final String REDIS_CHANGE_PASSWORD_PREFIX="CPW";

    void signUpUser(Member member);

    Member loginUser(String id, String password) throws Exception;

    void sendVerificationMail(Member member) throws NotFoundException;

    void verifyEmail(String key) throws UserNotFoundException;

    void modifyUserRole(Member member, UserRole userRole);

    Member findByUsername(String username) throws UserNotFoundException;

    void isPasswordKeyValidate(String key)throws UserNotFoundException;

    void changePassword(Member member,String password) throws UserNotFoundException;

    void requestChangePassword(Member member) throws UserNotFoundException;
}

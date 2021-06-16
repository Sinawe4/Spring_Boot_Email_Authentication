package com.example.jwt.service;

public interface EmailService {
    void sendMail (String to, String sub, String text);
}

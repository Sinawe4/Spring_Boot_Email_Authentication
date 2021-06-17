package com.example.jwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestChangePasswordDto {
    String username;
    String password;
}

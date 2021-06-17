package com.example.jwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestChangePasswordEmailDto {
    private String username;
    private String email;
}

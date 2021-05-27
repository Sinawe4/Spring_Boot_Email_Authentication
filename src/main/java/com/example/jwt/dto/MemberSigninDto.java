package com.example.jwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSigninDto {
    public String username;
    public String password;

    public MemberSigninDto(String username, String password){
        this.username = username;
        this.password = password;
    }
}

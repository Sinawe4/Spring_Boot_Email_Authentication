package com.example.jwt.dto;

import com.example.jwt.domain.Member;
import com.example.jwt.domain.Salt;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDto {
    private String username;
    private String password;

    private String name;

    private String email;

    private String address;

    private Salt salt;

    public Member toEntity(){
        return Member.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .address(address)
                .salt(salt)
                .build();
    }
}

package com.example.jwt.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Response {
    private String response;
    private String message;
    private Object data;

    public Response(String response, String message, Object data){
        this.response = response;
        this.message = message;
        this.data = data;
    }
}
package com.example.jwt.controller;

import com.example.jwt.advice.exception.*;
import com.example.jwt.domain.response.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/exception")
public class ExceptionController {
    // userNotFoundException
    @GetMapping(value = "/userNotFound")
    public CommonResult userNotFoundException() {
        throw new UserNotFoundException();
    }

    @GetMapping(value = "/userAlreadyExists")
    public CommonResult userAlreadyExistsException() {
        throw new UserAlreadyExistsException();
    }

    @GetMapping(value = "/access-token-expired")
    public CommonResult accessTokenExpired(){throw new AccessTokenExpiredException();}

    @GetMapping(value = "/invalid-token")
    public CommonResult invalidToken(){throw new InvalidTokenException();}

    @GetMapping(value = "/userLoginFailed")
    public CommonResult answerAlreadyExists(){throw new UserLoginFailedException();}

}


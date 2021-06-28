package com.example.jwt.advice;

import com.example.jwt.advice.exception.*;
import com.example.jwt.domain.response.CommonResult;
import com.example.jwt.domain.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;
    private final MessageSource messageSource;

    // code정보에 해당하는 메시지를 조회합니다.
    private String getMessage(String code) {
        return getMessage(code, null);
    }
    // code정보, 추가 argument로 현재 locale에 맞는 메시지를 조회합니다.
    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult defaultException(HttpServletRequest request, Exception e){
        return responseService.getFailResult(Integer.valueOf(getMessage("unKnown.code")), e.getMessage());
    }
    //사용자를 찾을 수 없습니다.
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult userNotFoundException(HttpServletRequest request, UserNotFoundException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(Integer.valueOf(getMessage("userNotFound.code")), getMessage("userNotFound.msg"));
    }
    // 유저가 이미 존재합니다.
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected CommonResult userAlreadtExistsException(HttpServletRequest request, UserAlreadyExistsException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(Integer.valueOf(getMessage("userAlreadyExists.code")), getMessage("userAlreadyExists.msg"));
    }
    // 로그인 실패.
    @ExceptionHandler(UserLoginFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult userLoginFailedException(HttpServletRequest request, UserLoginFailedException e){
        return responseService.getFailResult(Integer.valueOf(getMessage("userLoginFailed.code")), getMessage("userLoginFailed.msg"));
    }

    //accessToken 이 만료되었습니다.
    @ExceptionHandler(AccessTokenExpiredException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResult accessTokenExpiredException(HttpServletRequest req, AccessTokenExpiredException e){
        return responseService.getFailResult(Integer.valueOf(getMessage("access-token-expired.code")), getMessage("access-token-expired.msg"));
    }

    //token(access, refresh)이 올바르지 않습니다..
    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult invalidToken(HttpServletRequest req, InvalidTokenException e){
        return responseService.getFailResult(Integer.valueOf(getMessage("invalid-token.code")), getMessage("invalid-token.msg"));
    }
}

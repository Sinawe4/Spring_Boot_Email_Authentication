package com.example.jwt.util;

import com.example.jwt.util.JwtUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class CookieUtil {

    public Cookie createCookie(String cookieName, String value){
        Cookie token = new Cookie(cookieName, value);
        token.setHttpOnly(true);
        token.setMaxAge((int) JwtUtil.TOKEN_VALIDATION_SECOND);
        token.setPath("/");
        return token;
    }

    public Cookie getCookie(HttpServletRequest request, String cookiename){
        final Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;z
        for (Cookie cookie : cookies){
            if (cookie.getName().equals(cookiename))
                return cookie;
        }
        return null;
    }
}

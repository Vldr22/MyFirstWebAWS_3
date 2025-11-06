package org.education.firstwebproject.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления JWT токенами в HTTP cookies.
 */
@Service
@RequiredArgsConstructor
public class JwtCookieService {

    @Getter
    public final static String COOKIE_NAME = "auth_token";

    private final JwtTokenService jwtTokenService;

    /**
     * Устанавливает JWT токен в HttpOnly cookie.
     */
    public void setAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);  // только HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) jwtTokenService.getExpirationSeconds());

        response.addCookie(cookie);
    }

    /**
     * Удаляет JWT токен из cookie (для logout).
     */
    public void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // удаляет cookie

        response.addCookie(cookie);
    }

    /**
     * Извлекает токен из cookie запроса.
     * Возвращает null если cookie не найден.
     */
    public String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }


}

package org.education.firstwebproject.service.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.audit.AuditableOperation;
import org.education.firstwebproject.model.enums.AuditOperation;
import org.education.firstwebproject.model.enums.UserRole;
import org.education.firstwebproject.model.entity.User;
import org.education.firstwebproject.model.dto.AuthRequest;
import org.education.firstwebproject.model.dto.LoginResponse;
import org.education.firstwebproject.security.JwtCookieService;
import org.education.firstwebproject.security.JwtTokenRedisService;
import org.education.firstwebproject.security.JwtTokenService;
import org.education.firstwebproject.exception.messages.Messages;
import org.education.firstwebproject.utils.MySecurityUtils;
import org.education.firstwebproject.service.user.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис аутентификации пользователей.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtTokenRedisService jwtTokenRedisService;
    private final UserService userService;
    private final JwtCookieService jwtCookieService;

    /**
     * Аутентифицирует пользователя и генерирует JWT токен.
     * Токен сохраняется в Redis для управления сессиями.
     */
    @AuditableOperation(operation = AuditOperation.LOGIN)
    public LoginResponse login(AuthRequest request, HttpServletResponse response) {
        User user = verifyCredentials(request.getUsername(), request.getPassword());
        UserRole role = getUserRole(user);

        String token = jwtTokenService.generateToken(user.getUsername(), role);
        jwtTokenRedisService.saveUserToken(
                user.getUsername(),
                token,
                jwtTokenService.getExpirationSeconds()
        );

        jwtCookieService.setAuthCookie(response, token);

        return new LoginResponse(user.getUsername(), role.getAuthority());
    }

    /**
     * Регистрирует нового пользователя с ролью ROLE_USER.
     */
    @AuditableOperation(operation = AuditOperation.REGISTER)
    public void register(AuthRequest request) {
        userService.createUser(request.getUsername(), request.getPassword());
        log.info("User registered successfully: {}", request.getUsername());
    }

    /**
     * Удаления и очистка токена из Cookie при выходе
     */
    @AuditableOperation(operation = AuditOperation.LOGOUT)
    public void logout(HttpServletResponse response) {
        String username = MySecurityUtils.getCurrentUsername();

        jwtTokenRedisService.deleteUserToken(username);
        jwtCookieService.clearAuthCookie(response);

        log.info("User {} logged out successfully", username);
    }

    /**
     * Старый токен удаляется из Redis.
     * Генерирует новый токен с обновленной ролью.
     */
    public LoginResponse refreshToken(String username, UserRole newRole, HttpServletResponse response) {
        String newToken = jwtTokenService.generateToken(username, newRole);

        jwtTokenRedisService.deleteUserToken(username);

        jwtTokenRedisService.saveUserToken(
                username,
                newToken,
                jwtTokenService.getExpirationSeconds()
        );

        jwtCookieService.setAuthCookie(response, newToken);
        return new LoginResponse(username, newRole.getAuthority());
    }

    private User verifyCredentials(String username, String password) {
        User user = userService.findByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException(Messages.INVALID_PASSWORD_OR_LOGIN);
        }
        return user;
    }

    private UserRole getUserRole(User user) {
        if (user.getRoles().isEmpty()) {
            return UserRole.ROLE_USER;
        }
        return UserRole.fromString(user.getRoles().iterator().next().getName());
    }
}


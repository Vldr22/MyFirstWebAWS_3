package org.education.firstwebproject.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exception.messages.Messages;
import org.education.firstwebproject.model.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Утилиты для работы с Spring Security Context и HTTP Request.
 */
@Slf4j
public class MySecurityUtils {

    private MySecurityUtils() {}

    public static final String UNKNOWN = "unknown";
    private static final String ANONYMOUS_USER = "anonymousUser";
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_X_REAL_IP = "X-Real-IP";

    public static Authentication getCurrentAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getCurrentUsername() {
        return getCurrentUserOptional()
                .map(User::getUsername)
                .orElse(null);
    }

    public static boolean hasRole(Authentication auth, String roleName) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(roleName));
    }

    public static boolean hasRole(String roleName) {
        return hasRole(getCurrentAuth(), roleName);
    }

    public static Long getCurrentUserId() {
        return getCurrentUserOptional()
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException(Messages.NOT_AUTHENTICATED));
    }

    public static Optional<User> getCurrentUserOptional() {
        try {
            Authentication auth = getCurrentAuth();

            if (auth != null && auth.isAuthenticated()
                    && !auth.getPrincipal().equals(ANONYMOUS_USER)) {

                Object principal = auth.getPrincipal();
                if (principal instanceof User) {
                    return Optional.of((User) principal);
                }
            }
        } catch (Exception e) {
            log.debug("Could not get current user", e);
        }
        return Optional.empty();
    }

    public static Optional<String> getClientIpOptional() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return Optional.empty();
            }

            HttpServletRequest request = attributes.getRequest();
            return Optional.of(extractClientIp(request));

        } catch (Exception e) {
            log.debug("Could not get client IP", e);
            return Optional.empty();
        }
    }

    public static String getClientIpOrDefault(String defaultIp) {
        return getClientIpOptional().orElse(defaultIp);
    }

    public static String getClientIpOrUnknown() {
        return getClientIpOrDefault(UNKNOWN);
    }

    private static String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader(HEADER_X_REAL_IP);
        if (realIp != null && !realIp.isEmpty()) {
            return realIp;
        }

        return request.getRemoteAddr();
    }
}

package org.education.firstwebproject.service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Утилиты для работы с Spring Security Context.
 */
public class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Authentication getCurrentAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getCurrentUsername() {
        return getCurrentAuth().getName();
    }

    public static boolean hasRole(Authentication auth, String roleName) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(roleName));
    }

    public static boolean hasRole(String roleName) {
        return hasRole(getCurrentAuth(), roleName);
    }
}

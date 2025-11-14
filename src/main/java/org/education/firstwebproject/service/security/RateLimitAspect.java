package org.education.firstwebproject.service.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.education.firstwebproject.exception.messages.Messages;
import org.education.firstwebproject.exception.ratelimit.RateLimitExceededException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;


/**
 * Аспект для ограничения частоты запросов (Rate Limiting).
 * <p>
 * Перехватывает методы, помеченные аннотацией {@link RateLimit},
 * и проверяет не превышен ли лимит запросов для данного пользователя.
 * Для авторизованных пользователей использует username, для анонимных - IP адрес.
 * <p>
 * Состояние счетчиков хранится в Redis с автоматическим истечением (TTL).
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitService rateLimitService;

    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_X_REAL_IP = "X-Real-IP";
    private static final String UNKNOWN = "unknown";
    private static final String IPV6_LOCALHOST_FULL = "0:0:0:0:0:0:0:1";
    private static final String IPV6_LOCALHOST_SHORT = "::1";
    private static final String IPV4_LOCALHOST = "127.0.0.1";

    /**
     * Проверяет лимит запросов перед выполнением метода.
     * <p>
     * Формирует уникальный ключ на основе идентификатора пользователя (username или IP)
     * и ключа из аннотации. Если лимит превышен, выбрасывает {@link RateLimitExceededException}.
     *
     * @param joinPoint точка перехвата выполнения метода
     * @return результат выполнения перехваченного метода
     * @throws Throwable если метод выбросил исключение или превышен лимит запросов
     */
    @Around("@annotation(org.education.firstwebproject.service.security.RateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RateLimit rateLimit = signature.getMethod().getAnnotation(RateLimit.class);

        String identifier = getUserIdentifier();
        String key = identifier + ":" + rateLimit.key();

        Duration window = Duration.of(rateLimit.window(), rateLimit.unit().toChronoUnit());

        boolean allowed = rateLimitService.isAllowed(key, rateLimit.requests(), window);

        if (!allowed) {
            throw new RateLimitExceededException(Messages.REQUEST_LIMIT_EXCEEDED);
        }

        return joinPoint.proceed();
    }

    /**
     * Получает идентификатор пользователя (username или IP)
     */
    private String getUserIdentifier() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return getClientIP(request);
        }

        return UNKNOWN;
    }

    /**
     * Получает реальный IP клиента (учитывая прокси)
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader(HEADER_X_FORWARDED_FOR);

        if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
            ip = ip.split(",")[0].trim();
            return normalizeIP(ip);
        }

        ip = request.getHeader(HEADER_X_REAL_IP);
        if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
            return normalizeIP(ip);
        }

        return normalizeIP(request.getRemoteAddr());
    }

    /**
     * Нормализует IP адрес (упрощает IPv6 localhost)
     */
    private String normalizeIP(String ip) {
        if (IPV6_LOCALHOST_FULL.equals(ip) || IPV6_LOCALHOST_SHORT.equals(ip)) {
            return IPV4_LOCALHOST;
        }
        return ip;
    }
}

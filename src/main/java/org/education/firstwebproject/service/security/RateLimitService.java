package org.education.firstwebproject.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Сервис для проверки ограничений частоты запросов.
 * Использует Redis для распределенного хранения счетчиков с автоматическим TTL.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedissonClient redissonClient;
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    /**
     * Проверяет не превышен ли лимит запросов.
     * При первом запросе создается счетчик с TTL, при превышении лимита возвращает false.
     *
     * @param key уникальный ключ (userId:endpoint или ip:endpoint)
     * @param maxRequests максимальное количество запросов
     * @param window временное окно действия лимита
     * @return true если запрос разрешен, false если лимит превышен
     */
    public boolean isAllowed(String key, int maxRequests, Duration window) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        RAtomicLong counter = redissonClient.getAtomicLong(redisKey);

        long currentCount = counter.incrementAndGet();

        if (currentCount == 1) {
            counter.expire(window);
        }

        boolean allowed = currentCount <= maxRequests;

        if (!allowed) {
            log.warn("Rate limit exceeded for key: {}, count: {}/{}", key, currentCount, maxRequests);
        }

        return allowed;
    }
}

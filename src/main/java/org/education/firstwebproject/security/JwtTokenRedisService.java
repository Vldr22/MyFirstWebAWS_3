package org.education.firstwebproject.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для управления JWT токенами в Redis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenRedisService {

    private final RedissonClient redissonClient;

    private static final String USER_TOKEN_PREFIX = "auth:token:";

    public void saveUserToken(String username, String token, long ttlSeconds) {
        String key = USER_TOKEN_PREFIX + username;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(token, Duration.ofSeconds(ttlSeconds));
        log.info("Token saved for user: {} with TTL: {} seconds", username, ttlSeconds);
    }

    public String getUserToken(String username) {
        String key = USER_TOKEN_PREFIX + username;
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    public boolean isTokenValid(String username, String token) {
        String storedToken = getUserToken(username);
        return token != null && token.equals(storedToken);
    }

    public void deleteUserToken(String username) {
        String key = USER_TOKEN_PREFIX + username;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.delete();
        log.info("Token deleted for user: {}", username);
    }
}

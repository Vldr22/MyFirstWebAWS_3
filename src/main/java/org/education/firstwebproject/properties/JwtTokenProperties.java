package org.education.firstwebproject.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Настройки JWT токенов.
 * <p>
 * Пример конфигурации в application.yml:
 * <pre>
 * jwt:
 *   secret-key: ${JWT_SECRET_KEY}
 *   expiration: 86400
 * </pre>
 */
@Getter
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JwtTokenProperties {

    @NotBlank(message = "JWT secret key is required")
    private final String secretKey;

    @Positive(message = "JWT expiration must be positive")
    private final long expiration;

    public JwtTokenProperties(String secretKey, long expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
    }
}

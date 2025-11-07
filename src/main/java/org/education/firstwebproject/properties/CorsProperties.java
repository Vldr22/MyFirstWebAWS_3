package org.education.firstwebproject.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Настройки CORS для API.
 * <p>
 * Пример конфигурации в application.yml:
 * <pre>
 * cors:
 *   allowed-origins:
 *     - http://localhost:3000
 *     - https://myapp.com
 *   allowed-methods:
 *     - GET
 *     - POST
 *     - PUT
 *     - DELETE
 * </pre>
 */
@Getter
@Validated
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    @NotEmpty(message = "At least one allowed origin is required")
    private final List<String> allowedOrigins;

    private final List<String> allowedMethods;

    private final List<String> allowedHeaders;
}

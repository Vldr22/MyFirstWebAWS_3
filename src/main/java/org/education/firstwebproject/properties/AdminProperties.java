package org.education.firstwebproject.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Настройки для инициализации админа.
 * <p>
 * Пример конфигурации в application.yml:
 * <pre>
 * admin:
 *   name: ${ADMIN_NAME}
 *   password: ${ADMIN_PASSWORD}
 * </pre>
 */
@Slf4j
@Getter
@Validated
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.admin")
public class AdminProperties {

    @NotBlank(message = "Admin name is required")
    private final String name;

    @NotBlank(message = "Admin password is required")
    private final String password;

}

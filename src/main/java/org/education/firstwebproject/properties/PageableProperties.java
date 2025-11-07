package org.education.firstwebproject.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Настройки пагинации для API.
 * <p>
 * Пример конфигурации в application.yml:
 * <pre>
 * spring:
 *   data:
 *     web:
 *       pageable:
 *         default-page-size: 10
 *         max-page-size: 100
 * </pre>
 */
@Getter
@Validated
@ConfigurationProperties(prefix = "app.pageable")
public class PageableProperties {

    @Min(value = 1, message = "Default page size must be at least 1")
    @Max(value = 100, message = "Default page size must not exceed 100")
    private final int defaultPageSize;

    @Min(value = 1, message = "Max page size must be at least 1")
    @Max(value = 100, message = "Max page size must not exceed 100")
    private final int maxPageSize;

    public PageableProperties(int defaultPageSize, int maxPageSize) {
        this.defaultPageSize = defaultPageSize;
        this.maxPageSize = maxPageSize;
    }
}

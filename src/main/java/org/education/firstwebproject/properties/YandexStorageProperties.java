package org.education.firstwebproject.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Настройки подключения к Yandex Object Storage.
 * <p>
 * Пример конфигурации в application.yml:
 * <pre>
 * yandex:
 *   storage:
 *     access-key: ${YANDEX_ACCESS_KEY}
 *     secret-key: ${YANDEX_SECRET_KEY}
 *     endpoint: <a href="https://storage.yandexcloud.net">...</a>
 *     region: ru-central1
 *     bucket-name: my-bucket
 * </pre>
 */
@Slf4j
@Getter
@Validated
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "yandex.storage")
public class YandexStorageProperties {

    @NotBlank(message = "Access key is required")
    private final String accessKey;

    @NotBlank(message = "Secret key is required")
    private final String secretKey;

    @NotBlank(message = "Endpoint is required")
    private final String endpoint;

    @NotBlank(message = "Region is required")
    private final String region;

    @NotBlank(message = "BucketName is required")
    private final String bucketName;

}

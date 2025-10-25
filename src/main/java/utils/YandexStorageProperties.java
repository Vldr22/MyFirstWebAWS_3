package utils;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Getter
@Validated
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

    @ConstructorBinding
    public YandexStorageProperties(String accessKey,
                             String secretKey,
                             String endpoint,
                             String region) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.endpoint = endpoint;
        this.region = region;
    }
}

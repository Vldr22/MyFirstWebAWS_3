package org.education.firstwebproject.utils;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Getter
@Validated
@ConfigurationProperties(prefix = "admin")
public class AdminProperties {

    @NotBlank(message = "Admin name is required")
    private final String name;

    @NotBlank(message = "Admin password is required")
    private final String password;

    public AdminProperties(String name, String password) {
        this.name = name;
        this.password = password;
    }
}

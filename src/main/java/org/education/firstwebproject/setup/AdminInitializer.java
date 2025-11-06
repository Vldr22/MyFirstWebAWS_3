package org.education.firstwebproject.setup;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.education.firstwebproject.service.user.UserService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.education.firstwebproject.properties.AdminProperties;

/**
 * Создает администратора при старте приложения, если его не существует.
 * Учетные данные берутся из конфигурации приложения
 *
 * @see AdminProperties
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AdminProperties.class)
public class AdminInitializer {

    private final UserService userService;
    private final AdminProperties adminProperties;

    @PostConstruct
    public void init() {
        String adminUsername = adminProperties.getName();
        boolean adminExists = userService.findAllUsers().stream()
                .anyMatch(user -> user.getUsername().equals(adminUsername));

        if (!adminExists) {
            userService.createAdmin(adminUsername, adminProperties.getPassword());
        }
    }
}

package org.education.firstwebproject.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.education.firstwebproject.model.User;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.education.firstwebproject.utils.AdminProperties;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({AdminProperties.class})
public class AdminInitializer {

    private final UserService userService;
    private final AdminProperties adminProperties;

    @PostConstruct
    public void init() throws RoleNotFoundException {
        boolean flag = false;
        List<User> list = userService.findAllUsers();

        for (User user : list) {
            if (user.getUsername().equals(adminProperties.getName())) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            userService.saveAdmin(new User(adminProperties.getName(), adminProperties.getPassword()));
        }
    }
}

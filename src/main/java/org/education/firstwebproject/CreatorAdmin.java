package org.education.firstwebproject;

import org.education.firstwebproject.dto.User;
import org.education.firstwebproject.service.userService.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreatorAdmin implements CommandLineRunner {

    private final UserService userService;

    @Value("${adminName}")
    private String adminName;

    @Value("${adminPassword}")
    private String adminPassword;

    public CreatorAdmin(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        boolean flag = false;
        List<User> list = userService.findAllUsers();

        for (User user : list) {
            if (user.getUsername().equals(adminName)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            userService.saveAdmin(new User(adminName, adminPassword));
        }
    }
}

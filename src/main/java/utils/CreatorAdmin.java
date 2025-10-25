package utils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.education.firstwebproject.model.User;
import org.education.firstwebproject.service.UserService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({AdminProperties.class})
public class CreatorAdmin {

    private final UserService userService;
    private final AdminProperties adminProperties;

    @PostConstruct
    public void init() {
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

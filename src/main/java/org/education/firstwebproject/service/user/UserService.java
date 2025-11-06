package org.education.firstwebproject.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exception.user.UserAlreadyExistsException;
import org.education.firstwebproject.exception.user.UserNotFoundException;
import org.education.firstwebproject.model.entity.Role;
import org.education.firstwebproject.model.entity.User;
import org.education.firstwebproject.model.enums.UserRole;
import org.education.firstwebproject.repository.RoleRepository;
import org.education.firstwebproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.education.firstwebproject.exception.messages.Messages;

import java.util.List;

/**
 * Сервис для управления пользователями и их ролями.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public void createUser(String username, String password) {
        createUserWithRole(username, password, UserRole.ROLE_USER);
        log.info("User created successfully: {}", username);
    }

    public void createAdmin(String username, String password) {
        createUserWithRole(username, password, UserRole.ROLE_ADMIN);
        log.info("Admin created successfully: {}", username);
    }

    private void createUserWithRole(String username, String password, UserRole role) {
        if (existsByUsername(username)) {
            throw new UserAlreadyExistsException(Messages.USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.getRoles().add(findRoleByName(role));

        userRepository.save(user);
    }

    public void updateUserRole(String username, UserRole newRole) {
        User user = findByUsername(username);
        Role role = findRoleByName(newRole);

        user.getRoles().clear();
        user.getRoles().add(role);

        userRepository.save(user);
        log.info("Role updated successfully for user: {}", user.getUsername());
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(Messages.USER_NOT_FOUND, username)));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public Role findRoleByName(UserRole userRole) {
        return roleRepository.findByName(userRole.getAuthority())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(Messages.ROLE_NOT_FOUND, userRole)));
    }
}
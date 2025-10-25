package org.education.firstwebproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exceptionHandler.RoleUpdateException;
import org.education.firstwebproject.exceptionHandler.UserAlreadyExistsException;
import org.education.firstwebproject.exceptionHandler.UserNotFoundException;
import org.education.firstwebproject.model.Role;
import org.education.firstwebproject.model.User;
import org.education.firstwebproject.model.UserRole;
import org.education.firstwebproject.service.repository.RoleRepository;
import org.education.firstwebproject.service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    public void registerUser(User user) throws RoleNotFoundException {

        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

        Role userRole = findRoleByName(UserRole.ROLE_USER);
        user.setRoles(Collections.singleton(userRole));
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());
    }

    public void updateUserRole(Long userId, UserRole userRole) throws RoleNotFoundException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Role newRole = findRoleByName(userRole);
        user.getRoles().clear();
        user.getRoles().add(newRole);

        userRepository.save(user);
        log.info("Role updated successfully for user: {}", user.getUsername());
    }

    public boolean hasRole(User user, String roleName) {
        if (user == null || user.getRoles() == null) {
            return false;
        }

        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    public void saveAdmin(User user) {

        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

        Optional<Role> adminRole = roleRepository.findByName("ADMIN");
        user.setRoles(Collections.singleton(adminRole.orElseThrow(() -> new RoleUpdateException(user.getUsername()))));
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        log.info("Admin created successfully: {}", user.getUsername());
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    private Role findRoleByName(UserRole userRole) throws RoleNotFoundException {
        return roleRepository.findByName(userRole.getAuthority())
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + userRole));
    }
}
package org.education.firstwebproject.service.userService;

import jakarta.transaction.Transactional;
import org.education.firstwebproject.dto.User;
import org.education.firstwebproject.service.userService.repository.RoleRepository;
import org.education.firstwebproject.service.userService.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Юзер не найден");
        }
        return user;
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        user.setRoles(Collections.singleton(roleRepository.getReferenceById(1L)));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public void updateUserRole(User user) {
        user.setRoles(Collections.singleton(roleRepository.getReferenceById(3L)));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void saveAdmin(User user) {
        user.setRoles(Collections.singleton(roleRepository.getReferenceById(2L)));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

}

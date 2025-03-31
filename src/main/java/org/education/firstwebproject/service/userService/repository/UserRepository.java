package org.education.firstwebproject.service.userService.repository;

import org.education.firstwebproject.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String name);

}

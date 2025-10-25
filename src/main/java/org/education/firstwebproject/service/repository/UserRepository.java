package org.education.firstwebproject.service.repository;

import org.education.firstwebproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String name);
}

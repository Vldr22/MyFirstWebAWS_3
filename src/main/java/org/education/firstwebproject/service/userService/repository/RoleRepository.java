package org.education.firstwebproject.service.userService.repository;

import org.education.firstwebproject.dto.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByName(String name);
}

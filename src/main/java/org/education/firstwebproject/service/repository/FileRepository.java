package org.education.firstwebproject.service.repository;

import org.education.firstwebproject.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    File findById(long id);

    Optional<File> findByName(String fileName);

    void deleteByName(String name);

}


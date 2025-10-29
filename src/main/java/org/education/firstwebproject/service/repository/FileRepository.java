package org.education.firstwebproject.service.repository;

import org.education.firstwebproject.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileMetadata, Long> {

    FileMetadata findById(long id);

    Optional<FileMetadata> findByName(String fileName);

    void deleteByName(String name);

}


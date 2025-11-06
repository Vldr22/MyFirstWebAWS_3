package org.education.firstwebproject.repository;

import jakarta.transaction.Transactional;
import org.education.firstwebproject.model.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    Optional<FileMetadata> findByUniqueName(String uniqueName);

    Optional<FileMetadata> findByOriginalName(String originalName);

    @Modifying
    @Transactional
    @Query("DELETE from FileMetadata f WHERE f.uniqueName = :uniqueName")
    void deleteByName(String uniqueName);

    boolean existsByFileHash(String fileHash);
}


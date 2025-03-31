package org.education.firstwebproject.service.filesService.repository;


import org.education.firstwebproject.dto.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

    File findById(long id);

    File findByName(String fileName);

}


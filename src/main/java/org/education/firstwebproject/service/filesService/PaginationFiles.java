package org.education.firstwebproject.service.filesService;

import org.education.firstwebproject.dto.File;
import org.education.firstwebproject.service.filesService.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PaginationFiles {

    private final FileRepository fileDataJPA;

    @Value("${pageSize}")
    private int pageSize;

    public PaginationFiles(FileRepository fileDataJPA) {
        this.fileDataJPA = fileDataJPA;
    }

    public Page<File> findFilesWithPagination(int offset) {
        return fileDataJPA.findAll(PageRequest.of(offset - 1, pageSize).withSort(Sort.by("id")));
    }

    public int getTotalPage() {
        return (fileDataJPA.findAll().size() + pageSize - 1) / pageSize;
    }

}

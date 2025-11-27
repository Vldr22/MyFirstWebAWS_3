package org.education.firstwebproject.controller;

import lombok.RequiredArgsConstructor;
import org.education.firstwebproject.model.dto.FileResponse;
import org.education.firstwebproject.service.file.FilePaginationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final FilePaginationService filePaginationService;

    @GetMapping()
    public Page<FileResponse> getFiles(Pageable pageable) {
        return filePaginationService.paginationFiles(pageable);
    }
}
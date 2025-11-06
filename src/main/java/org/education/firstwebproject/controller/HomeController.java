package org.education.firstwebproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.model.response.FileResponse;
import org.education.firstwebproject.service.file.FilePaginationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
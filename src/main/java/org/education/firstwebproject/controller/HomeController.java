package org.education.firstwebproject.controller;

import lombok.RequiredArgsConstructor;
import org.education.firstwebproject.model.response.FileResponse;
import org.education.firstwebproject.service.file.FilePaginationService;
import org.education.firstwebproject.service.security.RateLimit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final FilePaginationService filePaginationService;

    @GetMapping()
    @RateLimit(requests = 30, window = 1, unit = TimeUnit.MINUTES, key = "public:home")
    public Page<FileResponse> getFiles(Pageable pageable) {
        return filePaginationService.paginationFiles(pageable);
    }
}
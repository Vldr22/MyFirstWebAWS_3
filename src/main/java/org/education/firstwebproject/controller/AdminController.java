package org.education.firstwebproject.controller;

import org.education.firstwebproject.service.filesService.StorageYandexService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Controller()
@RequestMapping("/upload")
public class AdminController {

    private final StorageYandexService yandexService;

    public AdminController(StorageYandexService yandexService) {
        this.yandexService = yandexService;
    }

    @GetMapping()
    public String adminUpload() {
        return "adminUpload";
    }

    @PostMapping("/file")
    @ResponseBody
    public ResponseEntity<Boolean> uploadFile(@RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(yandexService.uploadFile(file), HttpStatus.OK);
    }

    @PostMapping("/multiple-files")
    @ResponseBody
    public ResponseEntity<List<String>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<String> fileList = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadFile(file);
            fileList.add(file.getOriginalFilename());
        }
        return new ResponseEntity<>(fileList, HttpStatus.OK);
    }
}

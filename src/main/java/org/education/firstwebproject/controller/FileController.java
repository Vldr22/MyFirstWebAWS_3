package org.education.firstwebproject.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.model.CommonResponse;

import org.education.firstwebproject.model.response.FileDownloadResponse;
import org.education.firstwebproject.model.response.LoginResponse;
import org.education.firstwebproject.model.response.MultipleUploadResponse;
import org.education.firstwebproject.service.file.FileManagementService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.education.firstwebproject.exception.messages.Messages;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileManagementService fileManagementService;

    private final static String FILE_MUST_BE_PROVIDED = "File must be provided";
    private final static String FILENAME_MUST_BE_NOT_EMPTY = "File name must not be empty";

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<LoginResponse> upload(
            @RequestParam("file")
            @NotNull(message = FILE_MUST_BE_PROVIDED)
            MultipartFile file, HttpServletResponse response) {
        LoginResponse loginResponse = fileManagementService.uploadSingleFile(file, response);
        return CommonResponse.success(loginResponse);
    }

    @PostMapping("/upload-multiple")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<List<MultipleUploadResponse>> uploadMultiple(
            @RequestParam("files")
            @NotNull(message = FILE_MUST_BE_PROVIDED)
            MultipartFile[] files
    ) {
        List<MultipleUploadResponse> multipleUploadResult = fileManagementService.uploadMultipleFiles(files);
        return CommonResponse.success(multipleUploadResult);
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> download(
            @RequestParam
            @NotBlank(message = FILENAME_MUST_BE_NOT_EMPTY)
            String fileName) {

        FileDownloadResponse response = fileManagementService.prepareFileDownload(fileName);
        ByteArrayResource resource = new ByteArrayResource(response.getContent());

        return ResponseEntity
                .ok()
                .contentLength(response.getSize())
                .header("Content-Type", response.getContentType())
                .header("Content-Disposition", "attachment; filename*=UTF-8''" + response.getFileName())
                .body(resource);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<String> delete(
            @RequestParam
            @NotBlank(message = FILENAME_MUST_BE_NOT_EMPTY)
            String fileName) {
        fileManagementService.deleteFile(fileName);
        return CommonResponse.success(String.format(Messages.FILE_DELETE_SUCCESS, fileName));
    }
}
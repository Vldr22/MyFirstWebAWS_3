package org.education.firstwebproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exceptionHandler.FileStorageException;
import org.education.firstwebproject.model.MultipleUploadResult;
import org.education.firstwebproject.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/upload")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final FileService fileUploadService;

    @GetMapping
    public String uploadPage() {
        return "adminUpload";
    }

    @PostMapping("/file")
    public String uploadSingleFile(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Пожалуйста, выберите файл");
            return "redirect:/upload";
        }

        try {
            String message = fileUploadService.uploadSingleFile(file);
            redirectAttributes.addFlashAttribute("success", message);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Не удалось загрузить файл");
        }
        return "redirect:/upload";
    }

    @PostMapping("/multiple-files")
    public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
                                      RedirectAttributes redirectAttributes) {

        if (files == null || files.length == 0) {
            redirectAttributes.addFlashAttribute("error", "Пожалуйста, выберите файлы");
            return "redirect:/upload";
        }

        MultipleUploadResult result = fileUploadService.uploadMultipleFiles(files);

        if (result.hasSuccesses() && !result.hasErrors()) {
            redirectAttributes.addFlashAttribute("success",
                    "Успешно загружено файлов: " + result.successCount());
        } else if (result.hasSuccesses()) {
            redirectAttributes.addFlashAttribute("success",
                    "Успешно загружено: " + result.successCount() + ", не удалось: " + result.failCount());
            redirectAttributes.addFlashAttribute("error", result.getErrorMessage());
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Не удалось загрузить файлы. " + result.getErrorMessage());
        }

        return "redirect:/upload";
    }

    @PostMapping("/delete/{fileName}")
    public String deleteFile(@PathVariable String fileName,
                             RedirectAttributes redirectAttributes) {
        try {
            fileUploadService.deleteFile(fileName);
            redirectAttributes.addFlashAttribute("success",
                    "Файл успешно удален: " + fileName);
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Не удалось удалить файл: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error deleting file: {}", fileName, e);
            redirectAttributes.addFlashAttribute("error",
                    "Произошла ошибка при удалении файла");
        }
        return "redirect:/upload";
    }
}
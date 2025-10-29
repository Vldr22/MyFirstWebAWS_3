package org.education.firstwebproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exceptionHandler.StorageException;
import org.education.firstwebproject.model.MultipleUploadResult;
import org.education.firstwebproject.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.education.firstwebproject.utils.FlashAttributes;
import org.education.firstwebproject.utils.Messages;

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
            redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILE_NOT_SELECTED);
            return "redirect:/upload";
        }

        try {
            fileUploadService.uploadSingleFile(file);
            redirectAttributes.addFlashAttribute(FlashAttributes.SUCCESS,
                    String.format(Messages.FILE_UPLOAD_SUCCESS, file.getOriginalFilename()));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILE_UPLOAD_ERROR);
        }
        return "redirect:/upload";
    }

    @PostMapping("/multiple-files")
    public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
                                      RedirectAttributes redirectAttributes) {

        if (files == null || files.length == 0) {
            redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILES_NOT_SELECTED);
            return "redirect:/upload";
        }

        MultipleUploadResult result = fileUploadService.uploadMultipleFiles(files);

        if (result.hasSuccesses() && !result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    FlashAttributes.SUCCESS,
                    String.format(Messages.FILES_UPLOAD_SUCCESS, result.successCount()));
        } else if (result.hasSuccesses()) {
            redirectAttributes.addFlashAttribute(
                    FlashAttributes.SUCCESS,
                    String.format(Messages.FILES_UPLOAD_PARTIAL,
                            result.successCount(), result.failCount()));
            redirectAttributes.addFlashAttribute(
                    FlashAttributes.ERROR,
                    String.format(Messages.FILES_UPLOAD_FAILED, result.getErrorMessage()));
        } else {
            redirectAttributes.addFlashAttribute(
                    FlashAttributes.ERROR,
                    Messages.FILES_UPLOAD_FAILED + result.getErrorMessage());
        }

        return "redirect:/upload";
    }

    @PostMapping("/delete/{fileName}")
    public String deleteFile(@PathVariable String fileName,
                             RedirectAttributes redirectAttributes) {
        try {
            fileUploadService.deleteFile(fileName);
            redirectAttributes.addFlashAttribute(
                    FlashAttributes.SUCCESS,
                    String.format(Messages.FILE_DELETE_SUCCESS, fileName));
        } catch (StorageException e) {
            redirectAttributes.addFlashAttribute(
                    FlashAttributes.ERROR,
                    String.format(Messages.FILE_DELETE_ERROR, fileName));
        } catch (Exception e) {
            log.error("Unexpected error deleting file: {}", fileName, e);
            redirectAttributes.addFlashAttribute(
                    FlashAttributes.ERROR,
                    String.format(Messages.FILE_DELETE_ERROR, fileName));
        }
        return "redirect:/upload";
    }
}
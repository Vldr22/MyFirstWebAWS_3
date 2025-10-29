package org.education.firstwebproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exceptionHandler.FileDownloadException;
import org.education.firstwebproject.exceptionHandler.StorageException;
import org.education.firstwebproject.model.User;
import org.education.firstwebproject.model.UserRole;
import org.education.firstwebproject.service.FileService;
import org.education.firstwebproject.service.PaginationFiles;
import org.education.firstwebproject.service.UserService;
import org.education.firstwebproject.utils.FlashAttributes;
import org.education.firstwebproject.utils.Messages;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static org.education.firstwebproject.utils.AppConstants.*;


@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final PaginationFiles paginationFilesJPA;
    private final UserService userService;
    private final FileService fileService;

    @GetMapping
    public String home(Model model) {
        return viewPage(1, model);
    }

    @GetMapping("/{pageNumber}")
    public String viewPage(@PathVariable int pageNumber, Model model) {
        if (pageNumber < 1) {
            return "redirect:/home";
        }

        int totalPages = paginationFilesJPA.getTotalPage();

        if (pageNumber > totalPages && totalPages > 0) {
            return "redirect:/home" + totalPages;
        }

        model.addAttribute("fileList", paginationFilesJPA.findFilesWithPagination(pageNumber).toList());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);

        return "home";
    }

    @GetMapping("/addFile")
    public String addFile(Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findUserByUsername(principal.getName());

        if (userService.hasRole(user, UserRole.ROLE_USER_ADDED.getAuthority())) {
            log.warn("User {} tried to access upload form but already uploaded a file", user.getUsername());
            redirectAttributes.addFlashAttribute(FlashAttributes.ERROR,
                    Messages.INABILITY_UPLOAD_MORE_THAN_ONE_FILE);
            return "redirect:/home";
        }
        return "addFile";
    }

    @PostMapping("/addFile")
    public String addFile(@RequestParam("addFile") MultipartFile file,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {

        User user = userService.findUserByUsername(principal.getName());
        try {
            user.isEnabled();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.USER_ALREADY_EXISTS);
            return "redirect:/home/addFile";
        }


        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILE_NOT_SELECTED);
            return "redirect:/home/addFile";
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILE_SIZE_EXCEEDED);
            return "redirect:/home/addFile";
        }

            try {
                fileService.uploadSingleFile(file);
                userService.updateUserRole(user.getId(), UserRole.ROLE_USER_ADDED);
                redirectAttributes.addFlashAttribute(
                        FlashAttributes.SUCCESS,
                        String.format(Messages.FILE_UPLOAD_SUCCESS, file.getOriginalFilename()));
            } catch (StorageException e) {
                log.error("FileStorageException for user {}: {}", principal.getName(), e.getMessage());
                redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILE_UPLOAD_ERROR);
            } catch (Exception e) {
                log.error("Unexpected error during file upload for user: {}", principal.getName(), e);
                redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILE_UPLOAD_ERROR);
            }
        return "redirect:/home";
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String filename) {
        try {
            byte[] data = fileService.downloadFile(filename);

            if (data == null || data.length == 0) {
                log.warn("File not found: {}", filename);
                return ResponseEntity.notFound().build();
            }

            ByteArrayResource resource = new ByteArrayResource(data);
            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (FileDownloadException e) {
            log.error("Error downloading file: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            log.error("Unexpected error downloading file: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
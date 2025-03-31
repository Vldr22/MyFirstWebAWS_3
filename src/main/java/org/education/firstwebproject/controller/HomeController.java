package org.education.firstwebproject.controller;

import org.education.firstwebproject.dto.User;
import org.education.firstwebproject.service.filesService.StorageYandexService;
import org.education.firstwebproject.service.filesService.PaginationFiles;
import org.education.firstwebproject.service.userService.UserService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final PaginationFiles paginationFilesJPA;
    private final UserService userService;
    private final StorageYandexService yandexService;

    public HomeController(PaginationFiles paginationFilesJPA,
                          UserService userService, StorageYandexService yandexService) {
        this.paginationFilesJPA = paginationFilesJPA;
        this.userService = userService;
        this.yandexService = yandexService;
    }

    @GetMapping
    public String home(Model model) {
        viewPage(1, model);
        return "home";
    }

    @GetMapping("/{pageNumber}")
    public String viewPage(@PathVariable int pageNumber, Model model) {
        int totalPages = paginationFilesJPA.getTotalPage();

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());

            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("fileList", paginationFilesJPA.findFilesWithPagination(pageNumber).toList());
        model.addAttribute("totalPages", totalPages);
        return "home";
    }

    @GetMapping("/addFile")
    public String addFile() {
        return "addFile";
    }

    @PostMapping("/addFile")
    public String addFile(@RequestParam("addFile") MultipartFile file,
                          Principal principal) {
        User user = userService.findUserByUsername(principal.getName());

        if (yandexService.uploadFile(file)) {
            userService.updateUserRole(user);
        }

        return "redirect:/login";
    }

    @GetMapping("/download{filename}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable("filename") String fileName) {
        byte[] data = yandexService.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}

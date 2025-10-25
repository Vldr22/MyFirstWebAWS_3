package org.education.firstwebproject.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException ex,
                                         RedirectAttributes redirectAttributes) {

        log.error("File size exceeds maximum limit");
        redirectAttributes.addFlashAttribute("error",
                "Файл слишком большой! Максимальный размер: 30MB " + ex.getMessage());
        return "redirect:/home/addFile";
    }

    @ExceptionHandler(FileStorageException.class)
    public String handleFileStorageException(FileStorageException ex,
                                             RedirectAttributes redirectAttributes) {
        log.error("File storage error", ex);
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/home";
    }

    @ExceptionHandler(FileDownloadException.class)
    public String handleFileDownloadException(FileDownloadException ex,
                                              RedirectAttributes redirectAttributes) {
        log.error("File download error", ex);
        redirectAttributes.addFlashAttribute("error", "Не удалось скачать файл");
        return "redirect:/home";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex,
                                                 RedirectAttributes redirectAttributes) {
        log.error("Invalid argument", ex);
        redirectAttributes.addFlashAttribute("error", "Некорректный запрос: " + ex.getMessage());
        return "redirect:/home";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex,
                                         RedirectAttributes redirectAttributes) {
        log.error("Unexpected error occurred", ex);
        redirectAttributes.addFlashAttribute("error",
                "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.");
        return "redirect:/home";
    }
}
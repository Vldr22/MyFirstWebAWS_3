package org.education.firstwebproject.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.education.firstwebproject.utils.AppConstants;
import org.education.firstwebproject.utils.FlashAttributes;
import org.education.firstwebproject.utils.Messages;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)  // 👈 Правильное название
    public String handleMaxSizeException(MaxUploadSizeExceededException ex,
                                         RedirectAttributes redirectAttributes) {
        log.error("File exceeds Spring max upload size: " + AppConstants.MAX_FILE_SIZE_MB + " MB", ex);
        redirectAttributes.addFlashAttribute(
                FlashAttributes.ERROR,
                "File is too large! max size: " + AppConstants.MAX_FILE_SIZE_MB + " MB");
        return "redirect:/upload";
    }

    @ExceptionHandler(FileSizeExceededException.class)
    public String handleFileSizeExceeded(FileSizeExceededException ex,
                                         RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(
                FlashAttributes.ERROR,
                String.format(Messages.FILE_SIZE_EXCEEDED)
        );
        return "redirect:/upload";
    }

    @ExceptionHandler(FileEmptyException.class)
    public String handleEmptyFile(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILE_EMPTY);
        return "redirect:/upload";
    }

    @ExceptionHandler(FileDownloadException.class)
    public String handleFileDownloadException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILE_DOWNLOAD_ERROR);
        return "redirect:/home";
    }

    @ExceptionHandler(FileUploadException.class)
    public String handleFileUploadException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILE_UPLOAD_ERROR);
        return "redirect:/upload";
    }

    @ExceptionHandler(FileDeleteException.class)
    public String handleFileDeleteException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILE_DELETE_ERROR);
        return "redirect:/home";
    }

    @ExceptionHandler(StorageException.class)
    public String handleStorageException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.FILE_STORAGE_ERROR);
        return "redirect:/home";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex,
                                                 RedirectAttributes redirectAttributes) {
        log.warn("Illegal argument: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(FlashAttributes.ERROR,
                Messages.INVALID_REQUEST + ex.getMessage());
        return "redirect:/home";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex,
                                         RedirectAttributes redirectAttributes) {
        log.error("Unexpected unhandled exception", ex);
        redirectAttributes.addFlashAttribute(FlashAttributes.ERROR, Messages.UNEXPECTED_ERROR);
        return "redirect:/upload";
    }
}
package org.education.firstwebproject.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.model.enums.AllowedExtension;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

/**
 * Валидатор для проверки загружаемых файлов.
 * Проверяет соответствие Content-Type и расширения файла разрешенным типам из {@link AllowedExtension}.
 */

@Slf4j
@RequiredArgsConstructor
public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private final FileSignatureValidator signatureValidator;

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            addViolation(context, "File is empty");
            return false;
        }

        String contentType = file.getContentType();
        String extension = getFileExtension(file.getOriginalFilename());

        if (contentType == null || extension.isEmpty()) {
            addViolation(context, "Invalid extension or content type");
            return false;
        }

        if (!isAllowed(extension, contentType)) {
            addViolation(context, "File type not allowed");
            return false;
        }

        try {
            if (!signatureValidator.isValidSignature(file.getBytes(), extension)) {
                addViolation(context, "File signature does not match extension");
                return false;
            }
        } catch (IOException e) {
            log.error("Error reading file bytes during validation", e);
            addViolation(context, "Error processing file");
            return false;
        }

        return true;
    }

    private boolean isAllowed(String extension, String contentType) {
        return Arrays.stream(AllowedExtension.values())
                .anyMatch(allowed ->
                        allowed.getContentType().equalsIgnoreCase(contentType) &&
                                allowed.getExtension().equalsIgnoreCase(extension)
                );
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isBlank()) return "";
        String extension = StringUtils.getFilenameExtension(filename);
        return extension != null ? extension : "";
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}

package org.education.firstwebproject.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * Валидатор для проверки загружаемых файлов.
 * Проверяет соответствие Content-Type и расширения файла разрешенным типам из {@link AllowedExtension}.
 */
public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        String extension = getFileExtension(file.getOriginalFilename());

        if (contentType == null || extension.isEmpty()) {
            return false;
        }

        return isAllowed(extension, contentType);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }
        String extension = StringUtils.getFilenameExtension(filename);
        return extension != null ? extension : "";
    }

    private boolean isAllowed(String extension, String contentType) {
        return Arrays.stream(AllowedExtension.values())
                .anyMatch(allowed ->
                        allowed.getContentType().equalsIgnoreCase(contentType) &&
                                allowed.getExtension().equalsIgnoreCase(extension)
                );
    }
}

package org.education.firstwebproject.validation;

import org.education.firstwebproject.model.enums.FileSignature;
import org.springframework.stereotype.Service;

@Service
public class FileSignatureValidator {

    public boolean isValidSignature(byte[] fileBytes, String extension) {
        String ext = extension.toLowerCase();

        return switch (ext) {
            case "pdf" -> FileSignature.PDF.matches(fileBytes);
            case "docx", "xlsx" -> FileSignature.ZIP.matches(fileBytes);
            case "png" -> FileSignature.PNG.matches(fileBytes);
            case "jpg", "jpeg" -> FileSignature.JPEG.matches(fileBytes);
            case "gif" -> FileSignature.GIF.matches(fileBytes);
            case "txt" -> validateTextFile(fileBytes);
            default -> fileBytes.length > 0;
        };
    }

    private boolean validateTextFile(byte[] fileBytes) {
        String content = new String(fileBytes).toLowerCase();
        return !content.startsWith("http://") &&
                !content.startsWith("https://") &&
                !content.contains("s3://");
    }
}

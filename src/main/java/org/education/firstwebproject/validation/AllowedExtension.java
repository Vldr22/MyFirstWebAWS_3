package org.education.firstwebproject.validation;

import lombok.Getter;

/**
 * Перечисление разрешенных расширений файлов с соответствующими Content-Type.
 */
@Getter
public enum AllowedExtension {

    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png"),
    GIF("gif", "image/gif"),
    WEBP("webp", "image/webp"),
    PDF("pdf", "application/pdf"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    DOC("doc", "application/msword"),
    TXT("txt", "text/plain"),
    MP4("mp4", "video/mp4"),
    ZIP("zip", "application/zip");

    private final String extension;
    private final String contentType;

    AllowedExtension(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }
}

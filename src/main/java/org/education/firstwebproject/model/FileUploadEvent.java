package org.education.firstwebproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadEvent {
    private Long fileId;
    private String s3Key;
    private String bucketName;
    private String originalFilename;
    private Long userId;
}

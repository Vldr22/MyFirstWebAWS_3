package org.education.firstwebproject.model.dto;

import lombok.*;

@Getter
@Builder
public class FileResponse {
    private String fileName;
    private String fileSize;
}

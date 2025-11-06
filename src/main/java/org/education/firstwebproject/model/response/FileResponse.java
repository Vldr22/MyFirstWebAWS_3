package org.education.firstwebproject.model.response;

import lombok.*;

@Getter
@Builder
public class FileResponse {
    private String fileName;
    private String fileSize;
}

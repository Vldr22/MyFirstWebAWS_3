package org.education.firstwebproject.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileDownloadResponse {
    private byte[] content;
    private String fileName;
    private String contentType;
    private long size;
}


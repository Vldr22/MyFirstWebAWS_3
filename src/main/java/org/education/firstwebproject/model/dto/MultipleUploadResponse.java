package org.education.firstwebproject.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.education.firstwebproject.model.enums.ResponseStatus;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultipleUploadResponse {

    private ResponseStatus status;
    private String originalFilename;
    private String message;

}
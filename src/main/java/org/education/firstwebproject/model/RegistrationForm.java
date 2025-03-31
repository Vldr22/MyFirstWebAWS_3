package org.education.firstwebproject.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationForm {

    @NotEmpty
    @Size(min = 3, max = 20)
    private String username;

    @NotEmpty
    @Size(min = 3, max = 20)
    private String password;
}
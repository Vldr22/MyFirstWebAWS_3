package org.education.firstwebproject.model.enums;

import lombok.Getter;

@Getter
public enum AuditOperation {
    UPLOAD("upload"),
    DOWNLOAD("download"),
    DELETE("delete"),

    REGISTER("register"),
    LOGIN("login"),
    LOGOUT("logout");

    private final String description;

    AuditOperation(String description) {
        this.description = description;
    }
}

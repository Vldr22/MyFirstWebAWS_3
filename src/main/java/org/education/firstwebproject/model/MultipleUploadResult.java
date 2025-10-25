package org.education.firstwebproject.model;

import java.util.List;

public record MultipleUploadResult(int successCount, List<String> errors) {

    public int failCount() {
        return errors.size();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasSuccesses() {
        return successCount > 0;
    }

    public String getErrorMessage() {
        return String.join(" ", errors);
    }
}

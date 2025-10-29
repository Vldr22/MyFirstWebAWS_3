package org.education.firstwebproject.model;

import java.util.List;

public record MultipleUploadResult(int successCount, List<String> filedFiles) {

    public int failCount() {
        return filedFiles.size();
    }

    public boolean hasErrors() {
        return !filedFiles.isEmpty();
    }

    public boolean hasSuccesses() {
        return successCount > 0;
    }

    public String getErrorMessage() {
        return String.join(", ", filedFiles);
    }
}

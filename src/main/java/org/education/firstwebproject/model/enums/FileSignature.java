package org.education.firstwebproject.model.enums;

import lombok.Getter;

@Getter
public enum FileSignature {

    PDF(new byte[]{(byte) 0x25, (byte) 0x50, (byte) 0x44, (byte) 0x46}),
    ZIP(new byte[]{(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04}),
    PNG(new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47}),
    JPEG(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}),
    GIF(new byte[]{(byte) 0x47, (byte) 0x49, (byte) 0x46});

    private final byte[] signature;

    FileSignature(byte[] signature) {
        this.signature = signature;
    }

    public boolean matches(byte[] fileBytes) {
        if (fileBytes.length < this.signature.length) {
            return false;
        }
        for (int i = 0; i < this.signature.length; i++) {
            if (fileBytes[i] != this.signature[i]) {
                return false;
            }
        }
        return true;
    }
}

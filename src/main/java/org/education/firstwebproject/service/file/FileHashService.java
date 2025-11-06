package org.education.firstwebproject.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.education.firstwebproject.exception.messages.Messages;
import org.education.firstwebproject.exception.validation.DuplicateFileException;
import org.education.firstwebproject.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с хешами файлов и проверки дубликатов.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileHashService {

    private final FileMetadataRepository fileMetadataRepository;

    public String calculateMD5(byte[] fileBytes) {
        return DigestUtils.md5Hex(fileBytes);
    }

    public void checkDuplicateInDatabase(String fileHash) {
        if (fileMetadataRepository.existsByFileHash(fileHash)) {
            log.error("Duplicate file hash found fileHash: {}", fileHash);
            throw new DuplicateFileException(Messages.FILE_ALREADY_BEEN_UPLOADED);
        }
    }



}

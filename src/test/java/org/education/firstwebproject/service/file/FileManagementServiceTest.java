package org.education.firstwebproject.service.file;

import com.github.javafaker.Faker;
import jakarta.servlet.http.HttpServletResponse;
import org.education.firstwebproject.exception.file.FileUploadException;
import org.education.firstwebproject.exception.messages.Messages;
import org.education.firstwebproject.exception.validation.DuplicateFileException;
import org.education.firstwebproject.model.enums.ResponseStatus;
import org.education.firstwebproject.model.response.LoginResponse;
import org.education.firstwebproject.model.response.MultipleUploadResponse;
import org.education.firstwebproject.service.security.FileSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileManagementServiceTest {

    @Mock
    private FileOperationFacadeService fileOperationFacadeService;

    @Mock
    private FileSecurityService fileSecurityService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private FileManagementService fileManagementService;

    private Faker faker;
    private MockMultipartFile testFile;
    private String testFileName;
    private byte[] testFileContent;
    private LoginResponse testLoginResponse;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        testFileName = faker.file().fileName();
        testFileContent = faker.lorem().paragraph().getBytes();
        testFile = new MockMultipartFile(
                "file", testFileName, "application/pdf", testFileContent
        );
        testLoginResponse = new LoginResponse(
                faker.name().username(),
                "ROLE_USER"
        );
    }

    private MockMultipartFile createFileWithName(String filename) {
        return new MockMultipartFile(
                "file", filename,
                "application/pdf",
                faker.lorem().paragraph().getBytes()
        );
    }

    @Test
    void uploadSingleFile_WhenPermissionGranted_ReturnsLoginResponse() {
        doNothing().when(fileSecurityService).checkUploadPermission();
        doNothing().when(fileOperationFacadeService).uploadFile(testFile);
        when(fileSecurityService.updateTokenAfterUpload(response)).thenReturn(testLoginResponse);

        LoginResponse result = fileManagementService.uploadSingleFile(testFile, response);

        InOrder inOrder = inOrder(fileSecurityService, fileOperationFacadeService);
        inOrder.verify(fileSecurityService).checkUploadPermission();
        inOrder.verify(fileOperationFacadeService).uploadFile(testFile);
        inOrder.verify(fileSecurityService).updateTokenAfterUpload(response);

        assertEquals(testLoginResponse.getLogin(), result.getLogin());
    }

    @Test
    void uploadSingleFile_WhenAccessDenied_ThrowsAccessDeniedException() {
        doThrow(RuntimeException.class)
                .when(fileSecurityService).checkUploadPermission();

        assertThrows(RuntimeException.class,
                () -> fileManagementService.uploadSingleFile(testFile, response));

        verify(fileOperationFacadeService, never()).uploadFile(any());
        verify(fileSecurityService, never()).updateTokenAfterUpload(any());
    }

    @Test
    void uploadSingleFile_WhenUploadFails_DoesNotUpdateToken() {
        doNothing().when(fileSecurityService).checkUploadPermission();
        doThrow(RuntimeException.class)
                .when(fileOperationFacadeService).uploadFile(testFile);

        assertThrows(RuntimeException.class,
                () -> fileManagementService.uploadSingleFile(testFile, response));

        verify(fileSecurityService, never()).updateTokenAfterUpload(any());
    }

    @Test
    void uploadMultipleFiles_WhenAllFilesValid_AllSuccessMultipleResponse() {

        MultipartFile[] files = new MultipartFile[] {
                createFileWithName(faker.file().fileName()),
                createFileWithName(faker.file().fileName()),
                createFileWithName(faker.file().fileName()),
        };

        doNothing().when(fileOperationFacadeService).uploadFile(any());

        List<MultipleUploadResponse> results =
                fileManagementService.uploadMultipleFiles(files);

        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch((result) ->
                result.getStatus().equals(ResponseStatus.SUCCESS)));
        verify(fileOperationFacadeService, times(3)).uploadFile(any());
    }

    @Test
    void uploadMultipleFiles_WhenPartialSuccess_PartialSuccessMultipleResponse() {

        MockMultipartFile validFile = createFileWithName("valid.pdf");
        MockMultipartFile emptyFile = createFileWithName("empty.pdf");
        MockMultipartFile duplicateFile = createFileWithName("duplicate.pdf");
        MultipartFile[] files = {validFile, emptyFile, duplicateFile};

        doNothing().when(fileOperationFacadeService).uploadFile(validFile);

        doThrow(RuntimeException.class).when(fileOperationFacadeService).uploadFile(emptyFile);
        doThrow(DuplicateFileException.class).when(fileOperationFacadeService).uploadFile(duplicateFile);

        List<MultipleUploadResponse> results =
                fileManagementService.uploadMultipleFiles(files);

        assertEquals(3, results.size());
        assertEquals(ResponseStatus.SUCCESS, results.get(0).getStatus());
        assertEquals(ResponseStatus.ERROR, results.get(1).getStatus());
        assertEquals(ResponseStatus.ERROR, results.get(2).getStatus());
    }

    @Test
    void uploadMultipleFiles_WhenAllError_ThrowFileUploadException() {
        MultipartFile[] files = {testFile};
        doThrow(FileUploadException.class).when(fileOperationFacadeService).uploadFile(any());
        assertThrows(FileUploadException.class, () -> fileManagementService.uploadMultipleFiles(files));
    }

    @Test
    void deleteFile_WhenFileExists_DeletesSuccessfully() {
        doNothing().when(fileOperationFacadeService).deleteFile(testFileName);
        fileManagementService.deleteFile(testFileName);
        verify(fileOperationFacadeService).deleteFile(testFileName);
    }

    @Test
    void deleteFile_WhenFileNotFound_ThrowsStorageException() {
        doThrow(RuntimeException.class).when(fileOperationFacadeService).deleteFile(testFileName);
        assertThrows(RuntimeException.class,() -> fileManagementService.deleteFile(testFileName));
    }




    /*public List<MultipleUploadResponse> uploadMultipleFiles(MultipartFile[] files) {
        List<MultipleUploadResponse> results = new ArrayList<>();
        int successCount = 0;

        for (MultipartFile file : files) {
            MultipleUploadResponse response = processSingleFile(file);
            results.add(response);
            if (response.getStatus() == ResponseStatus.SUCCESS) {
                successCount++;
            }
        }

        if (successCount == 0) {
            throw new FileUploadException(Messages.FILES_UPLOAD_ERROR);
        }

        return results;
    }*/

}

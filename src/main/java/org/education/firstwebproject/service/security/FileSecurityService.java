package org.education.firstwebproject.service.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.education.firstwebproject.exception.messages.Messages;
import org.education.firstwebproject.exception.validation.FileUploadLimitExceededException;
import org.education.firstwebproject.model.enums.UserRole;
import org.education.firstwebproject.model.dto.LoginResponse;
import org.education.firstwebproject.service.auth.AuthService;
import org.education.firstwebproject.service.user.UserService;
import org.education.firstwebproject.utils.MySecurityUtils;
import org.springframework.stereotype.Service;

/**
 * Сервис проверки прав доступа к файлам.
 */
@Service
@RequiredArgsConstructor
public class FileSecurityService {

    private final UserService userService;
    private final AuthService authService;

    /**
     *  Проверяет право пользователя на загрузку файла.
     *  @throws FileUploadLimitExceededException если пользователь (не админ) уже загружал файл
     */
    public void checkUploadPermission() {
        if (MySecurityUtils.hasRole(UserRole.ROLE_ADMIN.getAuthority())) {
            return;
        }

        if (MySecurityUtils.hasRole(UserRole.ROLE_USER_ADDED.getAuthority())) {
            throw new FileUploadLimitExceededException(
                    Messages.INABILITY_UPLOAD_MORE_THAN_ONE_FILE);
        }
    }

    /**
     * Обновляет роль пользователя и возвращает новый токен после успешной загрузки.
     * Для админа возвращает текущий токен без изменений.
     */
    public LoginResponse updateTokenAfterUpload(HttpServletResponse response) {
        String username = MySecurityUtils.getCurrentUsername();

        if (MySecurityUtils.hasRole(UserRole.ROLE_ADMIN.getAuthority())) {
            return new LoginResponse(username, UserRole.ROLE_ADMIN.getRoleName());
        }

        userService.updateUserRole(username, UserRole.ROLE_USER_ADDED);
        return authService.refreshToken(username, UserRole.ROLE_USER_ADDED, response);
    }
}

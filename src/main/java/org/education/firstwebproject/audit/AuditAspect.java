package org.education.firstwebproject.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.education.firstwebproject.model.dto.AuthRequest;
import org.education.firstwebproject.model.enums.AuditOperation;
import org.education.firstwebproject.model.enums.ResponseStatus;
import org.education.firstwebproject.utils.MySecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Аудит критичных операций.
 * Логирует: REGISTER, LOGIN (ошибки), UPLOAD, DOWNLOAD, DELETE, LOGOUT
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditEventPublisher auditEventPublisher;

    @Around("@annotation(org.education.firstwebproject.audit.AuditableOperation)")
    public Object auditOperation(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AuditableOperation auditAnnotation = signature.getMethod()
                .getAnnotation(AuditableOperation.class);

        AuditOperation operation = auditAnnotation.operation();

        try {
            Object result = joinPoint.proceed();

            if (operation != AuditOperation.LOGIN) {
                String identifier = extractIdentifier(operation, joinPoint.getArgs());
                auditEventPublisher.publish(this, operation, identifier,
                        ResponseStatus.SUCCESS, null);
            }

            return result;

        } catch (Exception e) {
            String identifier = extractIdentifier(operation, joinPoint.getArgs());
            auditEventPublisher.publish(this, operation, identifier,
                    ResponseStatus.FAILED, e.getMessage());
            throw e;
        }
    }

    private String extractIdentifier(AuditOperation operation, Object[] args) {
        if (operation == AuditOperation.REGISTER || operation == AuditOperation.LOGIN) {
            return extractUsername(args);
        }
        return extractFileName(args);
    }

    private String extractUsername(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof AuthRequest) {
                return ((AuthRequest) arg).getUsername();
            }
        }
        return MySecurityUtils.UNKNOWN;
    }

    private String extractFileName(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof String && !((String) arg).trim().isEmpty()) {
                return ((String) arg).trim();
            }
            if (arg instanceof MultipartFile) {
                String name = ((MultipartFile) arg).getOriginalFilename();
                if (name != null && !name.isEmpty()) {
                    return name;
                }
            }
        }
        return MySecurityUtils.UNKNOWN;
    }
}

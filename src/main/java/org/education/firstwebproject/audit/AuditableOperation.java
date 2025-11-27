package org.education.firstwebproject.audit;

import org.education.firstwebproject.model.enums.AuditOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditableOperation {
    AuditOperation operation();
}

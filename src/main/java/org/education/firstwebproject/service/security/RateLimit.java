package org.education.firstwebproject.service.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Аннотация для ограничения частоты запросов к методу.
 * Для авторизованных пользователей лимит применяется по username, для анонимных - по IP.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * Максимальное количество запросов в заданном окне.
     */
    int requests();

    /**
     * Длительность временного окна.
     */
    long window();

    /**
     * Единица измерения времени для window. По умолчанию: минуты.
     */
    TimeUnit unit() default TimeUnit.MINUTES;

    /**
     * Уникальный ключ для группировки endpoint'ов (например, "auth:login", "file:upload").
     */
    String key();

}

package org.education.firstwebproject.exceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static class ErrorBody {

        private final String errorName;
        private final String errorDetails;

        public ErrorBody(String errorName, String errorDetails) {
            this.errorName = errorName;
            this.errorDetails = errorDetails;
        }

        @ExceptionHandler(Exception.class)
        public final ResponseEntity<ErrorBody> handleAllExceptions(Exception e) {
            logger.error("У нас проблема!", e);
            return new ResponseEntity<>(new ErrorBody
                    ("common_error", "Internal Server Error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }

        @ExceptionHandler(RandomHillelException.class)
        public final ResponseEntity<ErrorBody> handleRuntimeException(RandomHillelException e) {
            logger.error("У нас проблема! ", e);
            return new ResponseEntity<>(new ErrorBody
                    ("common_error", "RandomHillelException"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(BadRequestException.class)
        public final ResponseEntity<ErrorBody> handleBadRequestException(BadRequestException e) {
            logger.error("У нас проблема! ", e);
            return new ResponseEntity<>(new ErrorBody
                    ("request_error", "incorrect request from the client"),
                    HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(NotFoundException.class)
        public final ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
            logger.error("У нас проблема! ", e);
            return new ResponseEntity<>(new ErrorBody
                    ("request_error_", "The requested resource was not found"),
                    HttpStatus.NOT_FOUND);
        }

        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public static class RandomHillelException extends RuntimeException {
            public RandomHillelException(String message) {
                super(message);
            }
        }

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public static class BadRequestException extends RuntimeException {
            public BadRequestException(String message) {
                super(message);
            }
        }

        @ResponseStatus(HttpStatus.NOT_FOUND)
        public static class NotFoundException extends RuntimeException {
            public NotFoundException(String message) {
                super(message);
            }
        }

    }
}

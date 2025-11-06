package org.education.firstwebproject.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.education.firstwebproject.model.enums.ResponseStatus;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;


/**
 * Унифицированный формат ответов API.
 *  * <p>
 *  * Примеры использования:
 *  * <pre>
 *  * return CommonResponse.success(data);
 *  * return CommonResponse.error(problemDetail);
 *  * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    private T data;
    private ResponseStatus status;
    private ProblemDetail problemDetail;
    private LocalDateTime timestamp;

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(
                data,
                ResponseStatus.SUCCESS,
                null,
                LocalDateTime.now());
    }

    public static <T> CommonResponse<T> error(ProblemDetail problemDetail) {
        return new CommonResponse<>(
                null,
                ResponseStatus.ERROR,
                problemDetail,
                LocalDateTime.now());
    }
}

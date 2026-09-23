package kr.co.gochang.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

/**
 * 프론트가 의존하는 응답 봉투. JSON 필드명(snake_case)과 의미는 기존 Header 클래스와 동일하다.
 * 조회 실패도 기존 계약대로 HTTP 200 + result_code=ERROR 로 내려간다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        LocalDateTime transactionTime,
        String resultCode,
        String description,
        T data,
        PageInfo pagination
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(LocalDateTime.now(), "OK", "OK", data, null);
    }

    public static <T> ApiResponse<java.util.List<T>> ok(Page<T> page) {
        return new ApiResponse<>(LocalDateTime.now(), "OK", "OK", page.getContent(), PageInfo.of(page));
    }

    public static <T> ApiResponse<T> error(String description) {
        return new ApiResponse<>(LocalDateTime.now(), "ERROR", description, null, null);
    }
}

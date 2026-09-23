package kr.co.gochang.api;

import kr.co.gochang.api.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** 예상 못 한 오류도 HTML 이 아니라 JSON 봉투로 내려준다. */
@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    /** 잘못된 sort 속성, 숫자가 아닌 id 등. InvalidDataAccessApiUsageException 은 @Query 에 없는 sort 속성이 붙었을 때다. */
    @ExceptionHandler({PropertyReferenceException.class, MethodArgumentTypeMismatchException.class,
            InvalidDataAccessApiUsageException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> badRequest(Exception e) {
        return ApiResponse.error("잘못된 요청 파라미터입니다");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> notFound(NoResourceFoundException e) {
        return ApiResponse.error("없는 경로입니다");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> internal(Exception e) {
        log.error("Unhandled exception", e);
        return ApiResponse.error("서버 오류가 발생했습니다");
    }
}

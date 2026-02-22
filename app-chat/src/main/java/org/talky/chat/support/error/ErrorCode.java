package org.talky.chat.support.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 공통 에러

    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", LogLevel.ERROR),

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력 형식이 올바르지 않습니다", LogLevel.INFO),

    DUPLICATED_RESOURCE(HttpStatus.BAD_REQUEST, "이미 사용 중인 리소스입니다", LogLevel.ERROR),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다", LogLevel.INFO),

    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다", LogLevel.INFO),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다", LogLevel.INFO),

    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다", LogLevel.INFO),

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다", LogLevel.ERROR);

    //

    private final HttpStatus httpStatus;
    private final String message;
    private final LogLevel logLevel;

    ErrorCode(HttpStatus httpStatus, String message, LogLevel logLevel) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.logLevel = logLevel;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }
}

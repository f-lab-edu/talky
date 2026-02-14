package org.talky.platform.support.error;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 공통 에러

    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", LogLevel.ERROR),

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력 형식이 올바르지 않습니다", LogLevel.INFO),

    DUPLICATED_RESOURCE(HttpStatus.BAD_REQUEST, "이미 사용 중인 리소스입니다", LogLevel.ERROR),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다", LogLevel.INFO),

    BANNED(HttpStatus.FORBIDDEN, "정지된 계정입니다", LogLevel.INFO),

    // TODO: 토큰 관련 에러코드 분리
    //  - EXPIRED_TOKEN: access token 만료 → 클라이언트가 refresh 시도
    //  - INVALID_TOKEN: 위변조/잘못된 형식 → 클라이언트가 재로그인
    //  - refresh 실패는 UNAUTHORIZED 유지 (어차피 재로그인이 답)

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
}

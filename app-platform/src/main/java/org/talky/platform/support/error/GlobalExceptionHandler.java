package org.talky.platform.support.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.talky.platform.support.response.ApiResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ApiResponse<Void>> handleCoreException(CoreException e) {
        ErrorCode errorCode = e.getErrorCode();
        logException(errorCode, e);
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }


    private void logException(ErrorCode errorCode, CoreException e) {
        LogLevel logLevel = errorCode.getLogLevel();

        // 마지막 인자로 Exception (e) 을 넘기면 스택트레이스 출력
        if (logLevel == LogLevel.ERROR) {
            log.error("[{}] {}", errorCode.name(), errorCode.getMessage(), e);
        } else if (logLevel == LogLevel.WARN) {
            log.warn("[{}] {}", errorCode.name(), errorCode.getMessage(), e);
        }
    }
}

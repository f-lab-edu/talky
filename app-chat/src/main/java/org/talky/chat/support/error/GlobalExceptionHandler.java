package org.talky.chat.support.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.talky.chat.support.response.ApiResponse;

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

        if (logLevel == LogLevel.ERROR) {
            log.error("[{}] {}", errorCode.name(), errorCode.getMessage(), e);
        } else if (logLevel == LogLevel.WARN) {
            log.warn("[{}] {}", errorCode.name(), errorCode.getMessage(), e);
        }
    }
}

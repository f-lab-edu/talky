package org.talky.platform.support.response;

import org.talky.platform.support.error.ErrorCode;

public record ErrorDetail(
        String code,
        String message,
        Object data
) {
    public static ErrorDetail from(ErrorCode errorCode) {
        return new ErrorDetail(errorCode.name(), errorCode.getMessage(), null);
    }

    public static ErrorDetail from(ErrorCode errorCode, Object data) {
        return new ErrorDetail(errorCode.name(), errorCode.getMessage(), data);
    }
}

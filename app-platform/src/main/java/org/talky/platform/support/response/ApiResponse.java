package org.talky.platform.support.response;

import org.talky.platform.support.error.ErrorCode;

public record ApiResponse<T>(
        ResultType result,
        T data,
        ErrorDetail error
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(ResultType.ERROR, null, ErrorDetail.from(errorCode));
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, Object errorData) {
        return new ApiResponse<>(ResultType.ERROR, null, ErrorDetail.from(errorCode, errorData));
    }
}

package org.talky.platform.app.vo;

import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

public record RegisterCommand(
        String loginId,
        String password,
        String nickname
) {
    public RegisterCommand {
        validateLoginId(loginId);
        validatePassword(password);
        validateNickname(nickname);
    }

    private static void validateLoginId(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new CoreException(ErrorCode.INVALID_INPUT);
        }
        if (loginId.length() < 4 || loginId.length() > 20) {
            throw new CoreException(ErrorCode.INVALID_INPUT);
        }
        if (!loginId.matches("^[a-zA-Z0-9]+$")) {
            throw new CoreException(ErrorCode.INVALID_INPUT);
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new CoreException(ErrorCode.INVALID_INPUT);
        }
        if (password.length() < 8 || password.length() > 20) {
            throw new CoreException(ErrorCode.INVALID_INPUT);
        }
    }

    private static void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new CoreException(ErrorCode.INVALID_INPUT);
        }
        if (nickname.length() < 2 || nickname.length() > 10) {
            throw new CoreException(ErrorCode.INVALID_INPUT);
        }
    }
}

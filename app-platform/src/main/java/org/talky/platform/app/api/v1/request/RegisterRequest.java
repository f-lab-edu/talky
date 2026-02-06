package org.talky.platform.app.api.v1.request;

import org.talky.platform.app.vo.RegisterCommand;

public record RegisterRequest(
        String loginId,
        String password,
        String nickname
) {
    public RegisterCommand toCommand() {
        return new RegisterCommand(loginId, password, nickname);
    }
}

package org.talky.chat.app.api.v1.request;

import org.talky.chat.support.error.CoreException;
import org.talky.chat.support.error.ErrorCode;

import java.util.List;

public record CreateChatRequest(
        List<String> inviteeTags,
        String chatName
) {
    public void validate() {
        if (inviteeTags == null) {
            throw new CoreException(ErrorCode.INVALID_INPUT);
        }
    }
}

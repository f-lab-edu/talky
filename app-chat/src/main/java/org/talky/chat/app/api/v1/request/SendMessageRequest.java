package org.talky.chat.app.api.v1.request;

import org.talky.chat.app.vo.SendMessageCommand;
import org.talky.chat.support.error.CoreException;
import org.talky.chat.support.error.ErrorCode;

public record SendMessageRequest(
        String idempotencyKey,
        String content
) {
    public void validate() {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new CoreException(ErrorCode.INVALID_INPUT);
        }
        if (content == null || content.isBlank()) {
            throw new CoreException(ErrorCode.INVALID_INPUT);
        }
    }

    public SendMessageCommand toCommand(Long senderId, Long chatId) {
        return new SendMessageCommand(senderId, chatId, idempotencyKey, content);
    }
}

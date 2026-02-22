package org.talky.chat.app.vo;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UserChat(
        Long id,
        Long userId,
        Long chatId,
        Instant createdAt,
        Instant updatedAt
) {}

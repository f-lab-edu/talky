package org.talky.chat.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.Chat;
import org.talky.chat.app.vo.ChatType;
import org.talky.chat.app.vo.UserChat;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatFactory {

    private final TsidGenerator tsidGenerator;

    public Chat create(Long creatorId, String chatName, List<Long> participantIds) {
        ChatType type = ChatType.from(participantIds.size());
        return Chat.builder()
                .chatId(tsidGenerator.generate())
                .creatorId(creatorId)
                .type(type)
                .chatName(chatName)
                .participantIds(participantIds)
                .build();
    }

    public List<UserChat> createUserChats(Long chatId, List<Long> participantIds) {
        return participantIds.stream()
                .map(userId -> UserChat.builder()
                        .id(tsidGenerator.generate())
                        .userId(userId)
                        .chatId(chatId)
                        .build())
                .toList();
    }
}

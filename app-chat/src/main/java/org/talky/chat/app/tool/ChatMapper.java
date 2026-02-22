package org.talky.chat.app.tool;

import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.Chat;
import org.talky.chat.app.vo.ChatType;
import org.talky.chat.storage.entity.ChatEntity;
import org.talky.chat.storage.enums.EChatType;

@Component
public class ChatMapper {

    public ChatEntity toEntity(Chat chat) {
        return ChatEntity.builder()
                .id(chat.chatId())
                .creatorId(chat.creatorId())
                .type(EChatType.valueOf(chat.type().name()))
                .chatName(chat.chatName())
                .participantIds(chat.participantIds())
                .build();
    }

    public Chat toVo(ChatEntity entity) {
        return Chat.builder()
                .chatId(entity.getId())
                .creatorId(entity.getCreatorId())
                .type(ChatType.valueOf(entity.getType().name()))
                .chatName(entity.getChatName())
                .participantIds(entity.getParticipantIds())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

package org.talky.chat.app.tool;

import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.UserChat;
import org.talky.chat.storage.entity.UserChatEntity;

@Component
public class UserChatMapper {

    public UserChatEntity toEntity(UserChat userChat) {
        return UserChatEntity.builder()
                .id(userChat.id())
                .userId(userChat.userId())
                .chatId(userChat.chatId())
                .build();
    }

    public UserChat toVo(UserChatEntity entity) {
        return UserChat.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .chatId(entity.getChatId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

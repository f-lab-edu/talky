package org.talky.chat.app.tool;

import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.Message;
import org.talky.chat.storage.entity.MessageEntity;

@Component
public class MessageMapper {

    public MessageEntity toEntity(Message message) {
        return MessageEntity.builder()
                .id(message.messageId())
                .chatId(message.chatId())
                .senderId(message.senderId())
                .content(message.content())
                .idempotencyKey(message.idempotencyKey())
                .build();
    }

    public Message toVo(MessageEntity entity) {
        return Message.builder()
                .messageId(entity.getId())
                .chatId(entity.getChatId())
                .senderId(entity.getSenderId())
                .content(entity.getContent())
                .idempotencyKey(entity.getIdempotencyKey())
                .sentAt(entity.getSentAt())
                .build();
    }
}

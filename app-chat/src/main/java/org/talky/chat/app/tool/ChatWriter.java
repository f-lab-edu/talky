package org.talky.chat.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.Chat;
import org.talky.chat.storage.entity.ChatEntity;
import org.talky.chat.storage.repository.ChatRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ChatWriter {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    public Mono<Chat> saveChat(Chat chat) {
        ChatEntity entity = chatMapper.toEntity(chat);
        return chatRepository.save(entity).map(chatMapper::toVo);
    }
}

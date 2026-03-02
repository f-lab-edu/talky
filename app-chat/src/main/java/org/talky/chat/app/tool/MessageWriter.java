package org.talky.chat.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.Message;
import org.talky.chat.storage.repository.MessageRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MessageWriter {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public Mono<Message> save(Message message) {
        return messageRepository.save(messageMapper.toEntity(message))
                .map(messageMapper::toVo);
    }
}

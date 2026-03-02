package org.talky.chat.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.Message;
import org.talky.chat.storage.repository.MessageRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MessageReader {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public Mono<Message> findByIdempotencyKey(String idempotencyKey) {
        return messageRepository.findByIdempotencyKey(idempotencyKey)
                .map(messageMapper::toVo);
    }
}

package org.talky.chat.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.Message;
import reactor.core.publisher.Mono;

// TODO: Redis TTL 기반으로 교체
@Component
@RequiredArgsConstructor
public class MessageIdempotencyChecker {

    private final MessageReader messageReader;

    public Mono<Message> findDuplicate(String idempotencyKey) {
        return messageReader.findByIdempotencyKey(idempotencyKey);
    }
}

package org.talky.chat.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.talky.chat.app.tool.MessageIdempotencyChecker;
import org.talky.chat.app.vo.Message;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MessageSendProcessor {

    private final MessageWriter messageWriter;
    private final MessageReader messageReader;

    public Mono<Message> process(Message message) {
        return messageWriter.save(message)
                .onErrorResume(DuplicateKeyException.class, e ->
                        messageReader.findByIdempotencyKey(message.idempotencyKey())
                );
        // TODO: WebSocket 브로드캐스트 - MessageBroadcaster 주입 후 .flatMap(saved -> broadcaster.publish(saved).thenReturn(saved)) 추가
    }
}

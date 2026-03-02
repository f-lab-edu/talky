package org.talky.chat.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.talky.chat.app.vo.SendMessageCommand;
import org.talky.chat.app.tool.MessageIdempotencyChecker;
import org.talky.chat.app.tool.MessageSendProcessor;
import org.talky.chat.app.tool.TsidGenerator;
import org.talky.chat.app.vo.Message;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final TsidGenerator tsidGenerator;
    private final MessageIdempotencyChecker idempotencyChecker;
    private final MessageSendProcessor messageSendProcessor;

    public Mono<Message> sendMessage(SendMessageCommand command) {
        Message message = Message.from(tsidGenerator.generate(), command);
        return idempotencyChecker.findDuplicate(message.idempotencyKey())
                .switchIfEmpty(messageSendProcessor.process(message));
    }
}

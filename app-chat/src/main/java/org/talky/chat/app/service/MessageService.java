package org.talky.chat.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.talky.chat.app.tool.ChatReader;
import org.talky.chat.app.tool.MessageReader;
import org.talky.chat.app.tool.MessageSendProcessor;
import org.talky.chat.app.tool.MessageWriter;
import org.talky.chat.app.tool.TsidGenerator;
import org.talky.chat.app.vo.Chat;
import org.talky.chat.app.vo.Message;
import org.talky.chat.app.vo.SendMessageCommand;
import org.talky.chat.support.error.CoreException;
import org.talky.chat.support.error.ErrorCode;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final TsidGenerator tsidGenerator;
    private final MessageWriter messageWriter;
    private final MessageReader messageReader;
    private final MessageSendProcessor messageSendProcessor;
    private final ChatReader chatReader;

    public Mono<Message> sendMessage(SendMessageCommand command) {
        Message message = Message.from(tsidGenerator.generate(), command);
        Mono<Chat> findChat = chatReader.findById(command.chatId());
        return findChat.flatMap(chat -> {
            validateSender(chat, command.senderId());
            return messageWriter.save(message)
                    .doOnNext(savedMessage -> messageSendProcessor.process(savedMessage, chat.participantIds()))
                    .onErrorResume(DuplicateKeyException.class, e -> messageReader.findByIdempotencyKey(message.idempotencyKey()));
        });
    }

    private void validateSender(Chat chat, Long senderId) {
        if (!chat.participantIds().contains(senderId)) {
            throw new CoreException(ErrorCode.FORBIDDEN);
        }
    }
}

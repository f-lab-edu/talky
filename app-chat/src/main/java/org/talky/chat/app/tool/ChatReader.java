package org.talky.chat.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.Chat;
import org.talky.chat.storage.repository.ChatRepository;
import org.talky.chat.support.error.CoreException;
import org.talky.chat.support.error.ErrorCode;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatReader {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    public Mono<Chat> findExistingChat(List<Long> participantIds) {
        return chatRepository.findByParticipantIds(participantIds, participantIds.size())
                .map(chatMapper::toVo);
    }

    public Mono<Chat> findById(Long chatId) {
        return chatRepository.findById(chatId)
                .map(chatMapper::toVo)
                .switchIfEmpty(Mono.error(new CoreException(ErrorCode.RESOURCE_NOT_FOUND)));
    }
}

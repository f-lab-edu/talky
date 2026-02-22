package org.talky.chat.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.Chat;
import org.talky.chat.storage.repository.ChatRepository;
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
}

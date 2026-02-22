package org.talky.chat.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.UserChat;
import org.talky.chat.storage.entity.UserChatEntity;
import org.talky.chat.storage.repository.UserChatRepository;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserChatWriter {

    private final UserChatRepository userChatRepository;
    private final UserChatMapper userChatMapper;

    public Mono<List<UserChat>> saveAllUserChats(List<UserChat> userChats) {
        List<UserChatEntity> entities = userChats.stream()
                .map(userChatMapper::toEntity)
                .toList();
        return userChatRepository.saveAll(entities)
                .map(userChatMapper::toVo)
                .collectList();
    }
}

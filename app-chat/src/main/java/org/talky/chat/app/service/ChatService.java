package org.talky.chat.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.talky.chat.app.tool.ChatFactory;
import org.talky.chat.app.tool.ChatReader;
import org.talky.chat.app.tool.ChatWriter;
import org.talky.chat.app.tool.PlatformApiClient;
import org.talky.chat.app.tool.UserChatWriter;
import org.talky.chat.app.vo.Chat;
import org.talky.chat.app.vo.ChatType;
import org.talky.chat.app.vo.UserChat;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatFactory chatFactory;
    private final ChatReader chatReader;
    private final ChatWriter chatWriter;
    private final UserChatWriter userChatWriter;
    private final PlatformApiClient platformApiClient;

    //TODO: 두 유저가 동시에 상대방과의 Direct 채팅방을 만드는 경우
    //TODO: Chat 객체는 저장됐는데, UserChat 저장 중에 서버가 뻗는 경우
    public Mono<Chat> createChat(Long creatorId, List<String> inviteeTags, String chatName) {
        return platformApiClient.validateCreatingChat(creatorId, inviteeTags) // Validation + 채팅 참여자 ID 반환
                .flatMap(participantIds -> {
                    ChatType type = ChatType.from(participantIds.size());

                    if (type == ChatType.SELF || type == ChatType.DIRECT) {
                        return chatReader.findExistingChat(participantIds)
                                .switchIfEmpty(persistChat(creatorId, chatName, participantIds));
                    }

                    return persistChat(creatorId, chatName, participantIds);
                });
    }

    private Mono<Chat> persistChat(Long creatorId, String chatName, List<Long> participantIds) {
        Chat chat = chatFactory.create(creatorId, chatName, participantIds);
        List<UserChat> userChats = chatFactory.createUserChats(chat.chatId(), chat.participantIds());
        return Mono.zip(
                chatWriter.saveChat(chat),
                userChatWriter.saveAllUserChats(userChats),
                (savedChat, ignored) -> savedChat
        );
    }
}

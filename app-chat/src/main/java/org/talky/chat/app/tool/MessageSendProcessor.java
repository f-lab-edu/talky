package org.talky.chat.app.tool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.talky.chat.app.vo.Message;
import org.talky.chat.app.vo.NewMessageEvent;
import org.talky.chat.support.util.JsonUtils;
import org.talky.chat.support.websocket.WebSocketSessionManager;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageSendProcessor {

    private final WebSocketSessionManager webSocketSessionManager;
    private final JsonUtils jsonUtils;

    // TODO: 발신자(message.senderId())는 API 응답으로 메시지를 받으므로 제외 고려
    public void process(Message message, List<Long> participantIds) {
        String json = jsonUtils.toJson(NewMessageEvent.from(message));

        for (Long receiverId : participantIds) {
            webSocketSessionManager.send(receiverId, json);
        }
    }
}

package org.talky.chat.support.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.talky.chat.support.util.JsonUtils;
import org.talky.chat.support.websocket.message.outbound.ConnectedMessage;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketSessionManager {

    private final JsonUtils jsonUtils;
    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<WebSocketSession, Sinks.Many<String>> sinks = new ConcurrentHashMap<>();

    public Mono<Void> connect(Long userId, WebSocketSession session) {
        sessions.computeIfAbsent(userId, id -> new CopyOnWriteArrayList<>()).add(session);
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        sinks.put(session, sink);
        sink.tryEmitNext(jsonUtils.toJson(ConnectedMessage.create()));
        return session.send(sink.asFlux().map(session::textMessage))
                .doFinally(signal -> disconnect(userId, session));
    }

    public void send(WebSocketSession session, String json) {
        Sinks.Many<String> sink = sinks.get(session);
        if (sink == null) return;
        concurrentEmit(sink, json);
    }

    public void send(Long userId, String json) {
        CopyOnWriteArrayList<WebSocketSession> userSessions = sessions.get(userId);
        if (userSessions == null) return;

        userSessions.stream()
                .map(sinks::get)
                .filter(Objects::nonNull)
                .forEach(sink -> concurrentEmit(sink, json));
    }

    private void concurrentEmit(Sinks.Many<String> sink, String json) {
        Sinks.EmitResult result;
        do {
            result = sink.tryEmitNext(json);
        } while (result == Sinks.EmitResult.FAIL_NON_SERIALIZED);
    }

    public void disconnect(Long userId, WebSocketSession session) {
        Sinks.Many<String> sink = sinks.remove(session);
        if (sink != null) {
            sink.tryEmitComplete();
        }
        CopyOnWriteArrayList<WebSocketSession> userSessions = sessions.get(userId);
        if (userSessions != null) {
            userSessions.remove(session);
            if (userSessions.isEmpty()) {
                sessions.remove(userId, userSessions);
            }
        }
    }
}

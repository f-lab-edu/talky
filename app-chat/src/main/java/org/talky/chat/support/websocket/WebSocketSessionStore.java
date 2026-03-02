package org.talky.chat.support.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketSessionStore {

    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    public void register(Long userId, WebSocketSession session) {
        sessions.computeIfAbsent(userId, id -> new CopyOnWriteArrayList<>()).add(session);
    }

    public void remove(Long userId, WebSocketSession session) {
        List<WebSocketSession> userSessions = sessions.get(userId);
        if (userSessions == null) {
            return;
        }
        userSessions.remove(session);
        if (userSessions.isEmpty()) {
            sessions.remove(userId, userSessions);
        }
    }

    public List<WebSocketSession> getSessions(Long userId) {
        return sessions.getOrDefault(userId, new CopyOnWriteArrayList<>());
    }

    public Map<Long, List<WebSocketSession>> getAllSessions() {
        return Collections.unmodifiableMap(sessions);
    }
}

package org.talky.chat.support.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import org.talky.auth.AccessToken;
import org.talky.auth.JwtTokenProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WebSocketDispatchHandler implements WebSocketHandler {

    private static final int HEARTBEAT_TIMEOUT_SECONDS = 60;

    private final WebSocketSessionStore sessionStore;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final Map<WsMessageType, WsMessageHandler<?, ?>> handlerMap;

    public WebSocketDispatchHandler(
            WebSocketSessionStore sessionStore,
            JwtTokenProvider jwtTokenProvider,
            ObjectMapper objectMapper,
            List<WsMessageHandler<?, ?>> handlers
    ) {
        this.sessionStore = sessionStore;
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(WsMessageHandler::supportedType, Function.identity()));
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return Mono.fromCallable(() -> extractUserId(session))
                .flatMap(userId -> {
                    sessionStore.register(userId, session);
                    log.info("[WebSocket 연결] userId={}, sessionId={}", userId, session.getId());
                    return session.send(buildResponseStream(session, userId))
                            .doFinally(signal -> onDisconnect(userId, session, signal));
                })
                .onErrorResume(e -> sendAuthFailed(session, e));
    }

    private Flux<WebSocketMessage> buildResponseStream(WebSocketSession session, Long userId) {
        Flux<WebSocketMessage> connected = Flux.just(session.textMessage(toJson(ConnectedMessage.create())));
        Flux<WebSocketMessage> inbound = session.receive()
                .timeout(Duration.ofSeconds(HEARTBEAT_TIMEOUT_SECONDS))
                .flatMap(msg -> processMessage(msg, session))
                .onErrorResume(TimeoutException.class, e -> {
                    log.info("[WebSocket 타임아웃] userId={}, sessionId={}", userId, session.getId());
                    return Flux.empty();
                });
        return Flux.concat(connected, inbound);
    }

    private void onDisconnect(Long userId, WebSocketSession session, SignalType signal) {
        sessionStore.remove(userId, session);
        log.info("[WebSocket 연결 해제] userId={}, sessionId={}, signal={}", userId, session.getId(), signal);
    }

    private Flux<WebSocketMessage> processMessage(WebSocketMessage rawMsg, WebSocketSession session) {
        String payload = rawMsg.getPayloadAsText();
        try {
            JsonNode node = objectMapper.readTree(payload);
            WsMessageType type = objectMapper.convertValue(node.get("type"), WsMessageType.class);
            WsMessageHandler<?, ?> handler = handlerMap.get(type);
            if (handler == null) {
                return Flux.empty();
            }
            return dispatch(handler, node, session);
        } catch (Exception e) {
            log.warn("[WebSocket 메시지 파싱 실패] sessionId={}, payload={}", session.getId(), payload);
            return Flux.empty();
        }
    }

    private <T, R> Flux<WebSocketMessage> dispatch(WsMessageHandler<T, R> handler, JsonNode node, WebSocketSession session) throws Exception {
        T message = objectMapper.treeToValue(node, handler.payloadType());
        return handler.handle(message)
                .map(response -> session.textMessage(toJson(response)));
    }

    private Mono<Void> sendAuthFailed(WebSocketSession session, Throwable e) {
        log.warn("[WebSocket 인증 실패] sessionId={}, reason={}", session.getId(), e.getMessage());
        return session.send(Mono.just(session.textMessage(toJson(AuthFailedMessage.create()))))
                .then(session.close(CloseStatus.POLICY_VIOLATION));
    }

    private Long extractUserId(WebSocketSession session) {
        String token = UriComponentsBuilder.fromUri(session.getHandshakeInfo().getUri())
                .build().getQueryParams().getFirst("token");
        if (token == null) {
            throw new IllegalArgumentException("WebSocket 토큰이 없습니다");
        }
        AccessToken accessToken = jwtTokenProvider.parseAccessToken(token);
        return accessToken.userId();
    }

    private String toJson(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("[WebSocket JSON 직렬화 실패]", e);
            return "{\"type\":\"ERROR\"}";
        }
    }
}

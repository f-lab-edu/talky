package org.talky.chat.support.websocket;

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
import org.talky.chat.support.util.JsonUtils;
import org.talky.chat.support.websocket.message.outbound.AuthFailMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WebSocketDispatcher implements WebSocketHandler {

    private static final int HEARTBEAT_TIMEOUT_SECONDS = 60;

    private final WebSocketSessionManager sessionManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final JsonUtils jsonUtils;
    private final Map<MessageType.In, WsMessageHandler<?, ?>> handlerMap;

    public WebSocketDispatcher(
            WebSocketSessionManager sessionManager,
            JwtTokenProvider jwtTokenProvider,
            ObjectMapper objectMapper,
            JsonUtils jsonUtils,
            List<WsMessageHandler<?, ?>> handlers
    ) {
        this.sessionManager = sessionManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
        this.jsonUtils = jsonUtils;
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(WsMessageHandler::supportedType, Function.identity()));
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return Mono.fromCallable(() -> extractUserId(session))
                .flatMap(userId -> {
                    log.info("[WebSocket 연결] userId={}, sessionId={}", userId, session.getId());
                    Mono<Void> outbound = sessionManager.connect(userId, session);

                    // Inbound Message 에 대한 응답 처리 pipe-line
                    Mono<Void> inbound = session.receive()
                            .timeout(Duration.ofSeconds(HEARTBEAT_TIMEOUT_SECONDS))
                            .onErrorResume(TimeoutException.class, e -> {
                                log.info("[WebSocket 타임아웃] userId={}, sessionId={}", userId, session.getId());
                                return Flux.empty();
                            })
                            .flatMap(msg -> processMessage(msg, session))
                            .doOnNext(json -> sessionManager.send(session, json))
                            .doFinally(signal -> sessionManager.disconnect(userId, session))
                            .then();

                    return Mono.when(outbound, inbound);
                })
                .onErrorResume(e -> sendAuthFailed(session, e));
    }

    private Flux<String> processMessage(WebSocketMessage rawMsg, WebSocketSession session) {
        String payload = rawMsg.getPayloadAsText();
        try {
            JsonNode node = objectMapper.readTree(payload);
            MessageType.In type = objectMapper.convertValue(node.get("type"), MessageType.In.class);
            WsMessageHandler<?, ?> handler = handlerMap.get(type);
            if (handler == null) {
                return Flux.empty();
            }
            return dispatch(handler, node);
        } catch (Exception e) {
            log.warn("[WebSocket 메시지 파싱 실패] sessionId={}, payload={}", session.getId(), payload);
            return Flux.empty();
        }
    }

    private <T, R> Flux<String> dispatch(WsMessageHandler<T, R> handler, JsonNode node) throws Exception {
        T message = objectMapper.treeToValue(node, handler.payloadType());
        return handler.handle(message).map(this::toJson);
    }

    private Mono<Void> sendAuthFailed(WebSocketSession session, Throwable e) {
        log.warn("[WebSocket 인증 실패] sessionId={}, reason={}", session.getId(), e.getMessage());
        return session.send(Mono.just(session.textMessage(toJson(AuthFailMessage.create()))))
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

    private String toJson(Object value) {
        return jsonUtils.toJson(value);
    }
}

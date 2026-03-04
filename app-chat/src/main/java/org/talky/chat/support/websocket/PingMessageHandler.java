package org.talky.chat.support.websocket;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class PingMessageHandler implements WsMessageHandler<PingMessage, PongMessage> {

    @Override
    public WsMessageType supportedType() {
        return WsMessageType.PING;
    }

    @Override
    public Class<PingMessage> payloadType() {
        return PingMessage.class;
    }

    @Override
    public Flux<PongMessage> handle(PingMessage payload) {
        return Flux.just(PongMessage.create());
    }
}

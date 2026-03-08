package org.talky.chat.support.websocket.handler;

import org.springframework.stereotype.Component;
import org.talky.chat.support.websocket.MessageType;
import org.talky.chat.support.websocket.WsMessageHandler;
import org.talky.chat.support.websocket.message.inbound.PingMessage;
import org.talky.chat.support.websocket.message.outbound.PongMessage;
import reactor.core.publisher.Flux;

@Component
public class PingMessageHandler implements WsMessageHandler<PingMessage, PongMessage> {

    @Override
    public MessageType.In supportedType() {
        return MessageType.In.PING;
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

package org.talky.chat.support.websocket;

import org.springframework.stereotype.Component;
import org.talky.chat.support.websocket.message.inbound.PingInMsg;
import org.talky.chat.support.websocket.message.outbound.PongOutMsg;
import reactor.core.publisher.Flux;

@Component
public class PingMessageHandler implements WsMessageHandler<PingInMsg, PongOutMsg> {

    @Override
    public WsMessageType supportedType() {
        return WsMessageType.PING;
    }

    @Override
    public Class<PingInMsg> payloadType() {
        return PingInMsg.class;
    }

    @Override
    public Flux<PongOutMsg> handle(PingInMsg payload) {
        return Flux.just(PongOutMsg.create());
    }
}

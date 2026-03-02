package org.talky.chat.support.websocket;

import reactor.core.publisher.Flux;

public interface WsMessageHandler<T> {

    WsMessageType supportedType();

    Class<T> payloadType();

    Flux<WsMessage> handle(T payload);
}

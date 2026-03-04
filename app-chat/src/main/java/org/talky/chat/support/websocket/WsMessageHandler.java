package org.talky.chat.support.websocket;

import reactor.core.publisher.Flux;

public interface WsMessageHandler<T, R> {

    WsMessageType supportedType();

    Class<T> payloadType();

    Flux<R> handle(T payload);
}

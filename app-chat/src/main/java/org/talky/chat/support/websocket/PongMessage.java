package org.talky.chat.support.websocket;

public record PongMessage(WsMessageType type) {

    public static PongMessage create() {
        return new PongMessage(WsMessageType.PONG);
    }
}

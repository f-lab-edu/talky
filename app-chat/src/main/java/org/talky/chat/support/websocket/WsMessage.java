package org.talky.chat.support.websocket;

public record WsMessage(WsMessageType type) {

    public static WsMessage pong() {
        return new WsMessage(WsMessageType.PONG);
    }

    public static WsMessage authFailed() {
        return new WsMessage(WsMessageType.AUTH_FAILED);
    }
}

package org.talky.chat.support.websocket;

public record AuthFailedMessage(WsMessageType type) {

    public static AuthFailedMessage create() {
        return new AuthFailedMessage(WsMessageType.AUTH_FAILED);
    }
}

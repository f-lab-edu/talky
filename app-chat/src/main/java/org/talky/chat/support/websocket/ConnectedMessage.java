package org.talky.chat.support.websocket;

public record ConnectedMessage(WsMessageType type) {

    public static ConnectedMessage create() {
        return new ConnectedMessage(WsMessageType.CONNECTED);
    }
}

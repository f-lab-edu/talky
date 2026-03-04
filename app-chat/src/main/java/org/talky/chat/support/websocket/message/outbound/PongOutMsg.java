package org.talky.chat.support.websocket.message.outbound;

import org.talky.chat.support.websocket.WsMessageType;

public record PongOutMsg(WsMessageType type) {

    public static PongOutMsg create() {
        return new PongOutMsg(WsMessageType.PONG);
    }
}

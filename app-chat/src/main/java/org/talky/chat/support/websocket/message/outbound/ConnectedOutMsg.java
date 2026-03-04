package org.talky.chat.support.websocket.message.outbound;

import org.talky.chat.support.websocket.WsMessageType;

public record ConnectedOutMsg(WsMessageType type) {

    public static ConnectedOutMsg create() {
        return new ConnectedOutMsg(WsMessageType.CONNECTED);
    }
}

package org.talky.chat.support.websocket.message.outbound;

import org.talky.chat.support.websocket.WsMessageType;

public record AuthFailOutMsg(WsMessageType type) {

    public static AuthFailOutMsg create() {
        return new AuthFailOutMsg(WsMessageType.AUTH_FAILED);
    }
}

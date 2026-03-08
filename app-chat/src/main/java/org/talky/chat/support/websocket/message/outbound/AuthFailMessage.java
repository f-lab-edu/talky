package org.talky.chat.support.websocket.message.outbound;

import org.talky.chat.support.websocket.MessageType;

public record AuthFailMessage(MessageType.Out type) {

    public static AuthFailMessage create() {
        return new AuthFailMessage(MessageType.Out.AUTH_FAILED);
    }
}

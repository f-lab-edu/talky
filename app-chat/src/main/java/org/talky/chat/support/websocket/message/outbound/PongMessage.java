package org.talky.chat.support.websocket.message.outbound;

import org.talky.chat.support.websocket.MessageType;

public record PongMessage(MessageType.Out type) {

    public static PongMessage create() {
        return new PongMessage(MessageType.Out.PONG);
    }
}

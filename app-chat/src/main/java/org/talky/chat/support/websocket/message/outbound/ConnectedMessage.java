package org.talky.chat.support.websocket.message.outbound;

import org.talky.chat.support.websocket.MessageType;

public record ConnectedMessage(MessageType.Out type) {

    public static ConnectedMessage create() {
        return new ConnectedMessage(MessageType.Out.CONNECTED);
    }
}

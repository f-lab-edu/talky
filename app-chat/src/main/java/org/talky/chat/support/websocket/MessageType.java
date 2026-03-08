package org.talky.chat.support.websocket;

public class MessageType {

    public enum In {
        PING
    }

    public enum Out {
        CONNECTED, AUTH_FAILED, PONG, NEW_MESSAGE, ERROR
    }
}

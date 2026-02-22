package org.talky.chat.app.vo;

public enum ChatType {
    SELF,
    DIRECT,
    GROUP;

    public static ChatType from(int participantCount) {
        return switch (participantCount) {
            case 1 -> SELF;
            case 2 -> DIRECT;
            default -> GROUP;
        };
    }
}

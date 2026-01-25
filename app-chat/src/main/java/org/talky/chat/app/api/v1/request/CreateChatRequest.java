package org.talky.chat.app.api.v1.request;

import java.util.List;

public record CreateChatRequest(
        List<String> participantTags,
        String channelName
) {
}

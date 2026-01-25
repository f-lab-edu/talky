package org.talky.chat.app.api.v1.response;

import java.util.List;

public record CreateChatResponse(
        String channelId,
        String type,
        String channelName,
        List<ParticipantInfo> participants
) {
}

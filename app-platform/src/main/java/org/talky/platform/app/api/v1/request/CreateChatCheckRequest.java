package org.talky.platform.app.api.v1.request;

import java.util.List;

public record CreateChatCheckRequest(
        Long creatorId,
        List<String> inviteeTags
) {
}

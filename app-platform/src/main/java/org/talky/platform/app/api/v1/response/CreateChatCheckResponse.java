package org.talky.platform.app.api.v1.response;

import org.talky.platform.app.vo.User;

import java.util.List;

public record CreateChatCheckResponse(
        String creatorId,
        List<String> inviteeIds
) {
    public static CreateChatCheckResponse from(Long creatorId, List<User> invitedUsers) {
        return new CreateChatCheckResponse(
                String.valueOf(creatorId),
                invitedUsers.stream().map(user -> String.valueOf(user.id())).toList()
        );
    }
}

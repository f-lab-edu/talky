package org.talky.platform.app.api.v1.response;

import org.talky.platform.app.vo.UserProfile;

public record UserProfileResponse(
        String nickname,
        String userTag,
        String profileMessage
) {
    public static UserProfileResponse from(UserProfile userProfile) {
        return new UserProfileResponse(
                userProfile.user().nickname(),
                userProfile.user().userTag(),
                userProfile.profile().profileMessage()
        );
    }
}

package org.talky.platform.app.tool;

import org.talky.platform.app.vo.Profile;
import org.talky.platform.storage.entity.ProfileEntity;

public class ProfileMapper {

    public static Profile toVo(ProfileEntity entity) {
        return new Profile(
                entity.getId(),
                entity.getUserId(),
                entity.getProfileMessage()
        );
    }

    public static ProfileEntity toEntity(Profile profile) {
        return ProfileEntity.builder()
                .id(profile.id())
                .userId(profile.userId())
                .profileMessage(profile.profileMessage())
                .build();
    }
}

package org.talky.platform.app.tool;

import org.talky.platform.app.vo.User;
import org.talky.platform.storage.entity.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .loginId(entity.getLoginId())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .userTag(entity.getUserTag())
                .deleted(entity.isDeleted())
                .build();
    }

    public static UserEntity toEntity(User domain) {
        return new UserEntity(
                domain.getLoginId(),
                domain.getPassword(),
                domain.getNickname(),
                domain.getUserTag()
        );
    }
}

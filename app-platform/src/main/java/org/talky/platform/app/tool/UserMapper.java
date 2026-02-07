package org.talky.platform.app.tool;

import org.talky.platform.app.vo.User;
import org.talky.platform.storage.entity.UserEntity;

public class UserMapper {

    public static User toVo(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .loginId(entity.getLoginId())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .userTag(entity.getUserTag())
                .role(entity.getRole())
                .status(entity.getStatus())
                .deleted(entity.isDeleted())
                .build();
    }

    public static UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.id())
                .loginId(domain.loginId())
                .password(domain.password())
                .nickname(domain.nickname())
                .userTag(domain.userTag())
                .role(domain.role())
                .build();
    }
}

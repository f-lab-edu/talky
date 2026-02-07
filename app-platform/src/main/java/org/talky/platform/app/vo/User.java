package org.talky.platform.app.vo;

import lombok.Builder;
import org.talky.auth.UserRole;

@Builder
public record User(
        Long id,
        String loginId,
        String password,
        String nickname,
        String userTag,
        UserRole role,
        String status,
        boolean deleted
) {
}

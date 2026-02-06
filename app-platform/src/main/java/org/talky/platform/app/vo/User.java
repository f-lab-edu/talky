package org.talky.platform.app.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

    private final Long id;
    private final String loginId;
    private final String password;
    private final String nickname;
    private final String userTag;
    private final boolean deleted;

    @Builder
    public User(Long id, String loginId, String password, String nickname, String userTag, boolean deleted) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.userTag = userTag;
        this.deleted = deleted;
    }
}

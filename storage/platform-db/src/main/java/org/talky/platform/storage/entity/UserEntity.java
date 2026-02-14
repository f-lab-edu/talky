package org.talky.platform.storage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.talky.auth.UserRole;
import org.talky.auth.UserStatus;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    private Long id;

    private String loginId;

    private String password;

    private String nickname;

    private String userTag;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Builder
    private UserEntity(Long id, String loginId, String password, String nickname,
                      String userTag, UserRole role, UserStatus status) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.userTag = userTag;
        this.role = role;
        this.status = status;
    }

    public void update(UserEntity toUpdate) {
        this.loginId = toUpdate.getLoginId();
        this.password = toUpdate.getPassword();
        this.nickname = toUpdate.getNickname();
        this.userTag = toUpdate.getUserTag();
        this.role = toUpdate.getRole();
        this.status = toUpdate.getStatus();
    }
}

package org.talky.platform.storage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.talky.auth.UserRole;

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

    private String status = "ACTIVE";//TODO: 수정필요

    private boolean deleted = false;

    @Builder
    private UserEntity(Long id, String loginId, String password, String nickname,
                      String userTag, UserRole role) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.userTag = userTag;
        this.role = role;
    }
}

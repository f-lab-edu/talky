package org.talky.platform.storage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;

    private String password;

    private String nickname;

    private String userTag;

    private boolean deleted = false;

    public UserEntity(String loginId, String password, String nickname, String userTag) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.userTag = userTag;
    }
}

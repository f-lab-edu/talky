package org.talky.platform.storage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileEntity {

    @Id
    private Long id;

    private Long userId;

    private String profileMessage;

    @Builder
    private ProfileEntity(Long id, Long userId, String profileMessage) {
        this.id = id;
        this.userId = userId;
        this.profileMessage = profileMessage;
    }

    public void update(ProfileEntity toUpdate) {
        this.profileMessage = toUpdate.getProfileMessage();
    }
}

package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.app.vo.Profile;
import org.talky.platform.storage.entity.ProfileEntity;
import org.talky.platform.storage.repository.ProfileRepository;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

@Component
@RequiredArgsConstructor
public class ProfileWriter {

    private final ProfileRepository profileRepository;

    public Profile save(Profile profile) {
        ProfileEntity entity = ProfileMapper.toEntity(profile);
        ProfileEntity saved = profileRepository.save(entity);
        return ProfileMapper.toVo(saved);
    }

    public void update(Profile profile) {
        ProfileEntity entity = profileRepository.findById(profile.id())
                .orElseThrow(() -> new CoreException(ErrorCode.RESOURCE_NOT_FOUND));
        ProfileEntity toUpdate = ProfileMapper.toEntity(profile);
        entity.update(toUpdate);
    }
}

package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.app.vo.Profile;
import org.talky.platform.storage.repository.ProfileRepository;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

@Component
@RequiredArgsConstructor
public class ProfileReader {

    private final ProfileRepository profileRepository;

    public Profile findByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .map(ProfileMapper::toVo)
                .orElseThrow(() -> new CoreException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}

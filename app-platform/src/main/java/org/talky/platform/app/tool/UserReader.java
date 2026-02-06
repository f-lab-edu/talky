package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.storage.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserReader {
    private final UserRepository userRepository;

    public boolean existLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    public boolean existsUserTag(String userTag) {
        return userRepository.existsByUserTag(userTag);
    }
}

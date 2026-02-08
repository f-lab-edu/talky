package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.app.vo.User;
import org.talky.platform.storage.repository.UserRepository;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

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

    public boolean existsId(Long id) {
        return userRepository.existsById(id);
    }

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .map(UserMapper::toVo)
                .orElseThrow(() -> new CoreException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toVo)
                .orElseThrow(() -> new CoreException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}

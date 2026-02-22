package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.auth.UserStatus;
import org.talky.platform.app.vo.User;
import org.talky.platform.storage.repository.UserRepository;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

import java.util.List;

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
        return userRepository.findByLoginIdAndStatusNot(loginId, UserStatus.DELETED)
                .map(UserMapper::toVo)
                .orElseThrow(() -> new CoreException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public User findById(Long id) {
        return userRepository.findByIdAndStatusNot(id, UserStatus.DELETED)
                .map(UserMapper::toVo)
                .orElseThrow(() -> new CoreException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public User findByUserTag(String userTag) {
        return userRepository.findByUserTagAndStatusNot(userTag, UserStatus.DELETED)
                .map(UserMapper::toVo)
                .orElseThrow(() -> new CoreException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public List<User> findAllByUserTags(List<String> userTags) {
        List<User> users = userRepository.findAllByUserTagInAndStatusNot(userTags, UserStatus.DELETED)
                .stream()
                .map(UserMapper::toVo)
                .toList();
        if (users.size() != userTags.size()) {
            throw new CoreException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return users;
    }
}

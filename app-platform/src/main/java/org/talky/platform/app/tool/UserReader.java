package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.app.vo.User;
import org.talky.platform.storage.repository.UserRepository;

import java.util.Optional;

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

    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .map(UserMapper::toVo);
    }
}

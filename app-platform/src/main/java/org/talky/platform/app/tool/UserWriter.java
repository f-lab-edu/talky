package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.app.vo.User;
import org.talky.platform.storage.entity.UserEntity;
import org.talky.platform.storage.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserWriter {

    private final UserRepository userRepository;

    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = userRepository.save(entity);
        return UserMapper.toVo(saved);
    }
}

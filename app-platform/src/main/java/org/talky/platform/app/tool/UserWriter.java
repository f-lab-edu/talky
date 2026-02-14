package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.app.vo.User;
import org.talky.platform.storage.entity.UserEntity;
import org.talky.platform.storage.repository.UserRepository;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

@Component
@RequiredArgsConstructor
public class UserWriter {

    private final UserRepository userRepository;

    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = userRepository.save(entity);
        return UserMapper.toVo(saved);
    }

    public void update(User user) {
        UserEntity entity = userRepository.findById(user.id())
                .orElseThrow(() -> new CoreException(ErrorCode.RESOURCE_NOT_FOUND));
        UserEntity toUpdate = UserMapper.toEntity(user);
        entity.update(toUpdate);
    }
}

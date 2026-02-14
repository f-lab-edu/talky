package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.app.vo.LoginSession;
import org.talky.platform.storage.entity.LoginSessionEntity;
import org.talky.platform.storage.repository.LoginSessionRepository;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginSessionWriter {

    private final LoginSessionRepository loginSessionRepository;

    public LoginSession save(LoginSession loginSession) {
        LoginSessionEntity entity = LoginSessionMapper.toEntity(loginSession);
        LoginSessionEntity saved = loginSessionRepository.save(entity);
        return LoginSessionMapper.toVo(saved);
    }

    public void update(LoginSession loginSession) {
        LoginSessionEntity entity = loginSessionRepository.findById(loginSession.id())
                .orElseThrow(() -> new CoreException(ErrorCode.RESOURCE_NOT_FOUND));
        LoginSessionEntity toUpdate = LoginSessionMapper.toEntity(loginSession);
        entity.update(toUpdate);
    }

    public void revokeAllByUserId(Long userId) {
        loginSessionRepository.revokeAllByUserId(userId, LocalDateTime.now());
    }
}

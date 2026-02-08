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

    public void revoke(String accessJti) {
        LoginSessionEntity entity = loginSessionRepository.findByAccessJti(accessJti)
                .orElseThrow(() -> new CoreException(ErrorCode.RESOURCE_NOT_FOUND));
        entity.revoke();
    }

    public void revokeAllByUserId(Long userId) {
        loginSessionRepository.revokeAllByUserId(userId, LocalDateTime.now());
    }
}

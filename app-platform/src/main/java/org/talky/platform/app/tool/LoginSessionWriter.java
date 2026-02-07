package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.app.vo.LoginSession;
import org.talky.platform.storage.entity.LoginSessionEntity;
import org.talky.platform.storage.repository.LoginSessionRepository;

@Component
@RequiredArgsConstructor
public class LoginSessionWriter {

    private final LoginSessionRepository loginSessionRepository;

    public LoginSession save(LoginSession loginSession) {
        LoginSessionEntity entity = LoginSessionMapper.toEntity(loginSession);
        LoginSessionEntity saved = loginSessionRepository.save(entity);
        return LoginSessionMapper.toVo(saved);
    }
}

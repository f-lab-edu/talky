package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.app.vo.LoginSession;
import org.talky.platform.storage.repository.LoginSessionRepository;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

@Component
@RequiredArgsConstructor
public class LoginSessionReader {

    private final LoginSessionRepository loginSessionRepository;

    public LoginSession getByRefreshJti(String refreshJti) {
        return loginSessionRepository.findByRefreshJti(refreshJti)
                .map(LoginSessionMapper::toVo)
                .orElseThrow(() -> new CoreException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}

package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.app.vo.User;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

@Component
@RequiredArgsConstructor
public class RegisterValidator {

    private final UserReader userReader;

    public void validate(User user) {
        if (userReader.existLoginId(user.loginId())) {
            throw new CoreException(ErrorCode.DUPLICATED_RESOURCE);
        }
    }
}

package org.talky.platform.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.talky.platform.app.vo.User;
import org.talky.platform.app.vo.RegisterCommand;
import org.talky.platform.app.tool.RegisterValidator;
import org.talky.platform.app.tool.UserReader;
import org.talky.platform.app.tool.UserTagGenerator;
import org.talky.platform.app.tool.UserWriter;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserReader userReader;
    private final UserWriter userWriter;
    private final UserTagGenerator userTagGenerator;
    private final RegisterValidator registerValidator;
    private final PasswordEncoder passwordEncoder;

    public boolean checkLoginId(String loginId) {
        return userReader.existLoginId(loginId);
    }

    public User register(RegisterCommand command) {
        String encodedPassword = passwordEncoder.encode(command.password());
        String userTag = userTagGenerator.generate();

        User user = User.builder()
                .loginId(command.loginId())
                .password(encodedPassword)
                .nickname(command.nickname())
                .userTag(userTag)
                .build();

        registerValidator.validate(user);

        return userWriter.save(user);
    }
}

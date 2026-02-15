package org.talky.platform.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.talky.auth.UserRole;
import org.talky.platform.app.tool.ProfileWriter;
import org.talky.platform.app.tool.RegisterValidator;
import org.talky.platform.app.tool.UserIdGenerator;
import org.talky.platform.app.tool.UserReader;
import org.talky.platform.app.tool.UserTagGenerator;
import org.talky.platform.app.tool.UserWriter;
import org.talky.platform.app.vo.Profile;
import org.talky.platform.app.vo.RegisterCommand;
import org.talky.platform.app.vo.User;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserReader userReader;
    private final UserWriter userWriter;
    private final ProfileWriter profileWriter;
    private final UserTagGenerator userTagGenerator;
    private final RegisterValidator registerValidator;
    private final PasswordEncoder passwordEncoder;
    private final UserIdGenerator userIdGenerator;

    @Transactional(readOnly = true)
    public boolean checkLoginId(String loginId) {
        return userReader.existLoginId(loginId);
    }

    @Transactional
    public User register(RegisterCommand command) {
        String encodedPassword = passwordEncoder.encode(command.password());
        String userTag = userTagGenerator.generate();
        Long userId = userIdGenerator.generate();
        User user = User.builder()
                .id(userId)
                .loginId(command.loginId())
                .password(encodedPassword)
                .nickname(command.nickname())
                .userTag(userTag)
                .role(UserRole.USER)
                .build();

        registerValidator.validate(user);
        User savedUser = userWriter.save(user);

        Profile profile = new Profile(userIdGenerator.generate(), savedUser.id(), "");
        profileWriter.save(profile);

        return savedUser;
    }
}

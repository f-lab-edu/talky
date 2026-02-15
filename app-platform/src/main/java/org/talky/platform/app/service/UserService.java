package org.talky.platform.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.talky.platform.app.tool.ProfileReader;
import org.talky.platform.app.tool.UserReader;
import org.talky.platform.app.vo.Profile;
import org.talky.platform.app.vo.User;
import org.talky.platform.app.vo.UserProfile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserReader userReader;
    private final ProfileReader profileReader;

    public User getMe(Long userId) {
        return userReader.findById(userId);
    }

    public UserProfile getUserProfile(String userTag) {
        User user = userReader.findByUserTag(userTag);
        Profile profile = profileReader.findByUserId(user.id());
        return new UserProfile(user, profile);
    }
}

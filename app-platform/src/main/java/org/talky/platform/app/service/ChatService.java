package org.talky.platform.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.talky.auth.UserStatus;
import org.talky.platform.app.tool.UserReader;
import org.talky.platform.app.vo.User;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserReader userReader;

    public void validateChatCreation(Long creatorId, List<String> inviteeTags) {
        User creator = userReader.findById(creatorId);
        if (creator.status() == UserStatus.BANNED) {
            throw new CoreException(ErrorCode.BANNED);
        }
    }
}

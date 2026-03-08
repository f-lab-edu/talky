package org.talky.platform.app.api.v1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import org.talky.auth.AuthUserId;
import org.talky.platform.app.api.v1.response.MyInfoResponse;
import org.talky.platform.app.api.v1.response.UserProfileResponse;
import org.talky.platform.app.service.UserService;
import org.talky.platform.app.vo.User;
import org.talky.platform.app.vo.UserProfile;
import org.talky.platform.support.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/@me")
    public ApiResponse<MyInfoResponse> getMe(
            @AuthUserId Long userId
    ) {
        User user = userService.getMe(userId);
        return ApiResponse.success(MyInfoResponse.from(user));
    }

    @GetMapping("/{userTag}/profile")
    public CompletableFuture<ApiResponse<UserProfileResponse>> getUserProfile(
            @PathVariable String userTag
    ) {
        return userService.getUserProfileAsync(userTag)
                .thenApply(userProfile -> ApiResponse.success(UserProfileResponse.from(userProfile)));
    }
}

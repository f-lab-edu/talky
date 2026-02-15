package org.talky.platform.app.api.v1.controller;

import org.springframework.web.bind.annotation.*;
import org.talky.auth.AuthUserId;
import org.talky.platform.app.api.v1.response.MyInfoResponse;
import org.talky.platform.app.api.v1.response.UserProfileResponse;
import org.talky.platform.support.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/@me")
    public ApiResponse<MyInfoResponse> getMe(
            @AuthUserId Long userId
    ) {
        // TODO: 실제 내 정보 조회 로직은 차후 구현
        MyInfoResponse response = new MyInfoResponse(
                "user1234",
                "홍길동",
                "홍길동#1234"
        );

        return ApiResponse.success(response);
    }

    @GetMapping("/{userTag}")
    public ApiResponse<UserProfileResponse> getUserProfile(
            @AuthUserId Long userId,
            @PathVariable String userTag
    ) {
        // TODO: 실제 프로필 조회 로직은 차후 구현
        UserProfileResponse response = new UserProfileResponse(
                "김철수",
                userTag,
                "코딩 중..."
        );

        return ApiResponse.success(response);
    }
}

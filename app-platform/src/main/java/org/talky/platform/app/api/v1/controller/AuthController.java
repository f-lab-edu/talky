package org.talky.platform.app.api.v1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.talky.platform.app.api.v1.request.LoginRequest;
import org.talky.platform.app.api.v1.request.RegisterRequest;
import org.talky.platform.app.api.v1.response.CheckLoginIdResponse;
import org.talky.platform.app.api.v1.response.LoginResponse;
import org.talky.platform.app.api.v1.response.RegisterResponse;
import org.talky.platform.support.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/check-login-id")
    public ApiResponse<CheckLoginIdResponse> checkLoginId(
            @RequestParam String loginId
    ) {
        // TODO: 실제 중복 체크 로직은 서비스 레이어에서 구현
        boolean available = true;

        return ApiResponse.success(new CheckLoginIdResponse(available));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {
        // TODO: 실제 회원가입 로직은 서비스 레이어에서 구현
        RegisterResponse response = new RegisterResponse(
                request.loginId(),
                request.nickname(),
                request.nickname() + "#1234"
        );

        return ApiResponse.success(response);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        // TODO: 실제 로그인 로직은 차후 구현
        LoginResponse response = new LoginResponse(
                request.loginId(),
                "홍길동",
                "홍길동#1234",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test"
        );

        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String authorization
    ) {
        // TODO: 실제 로그아웃 로직은 차후 구현
        return ApiResponse.success(null);
    }
}

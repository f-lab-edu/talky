package org.talky.platform.app.api.v1.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talky.platform.app.api.ApiFraudChecker;
import org.talky.platform.app.api.v1.request.LoginRequest;
import org.talky.platform.app.api.v1.request.RegisterRequest;
import org.talky.platform.app.api.v1.response.CheckLoginIdResponse;
import org.talky.platform.app.api.v1.response.LoginResponse;
import org.talky.platform.app.api.v1.response.RegisterResponse;
import org.talky.platform.app.service.AuthService;
import org.talky.platform.app.vo.User;
import org.talky.platform.support.response.ApiResponse;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ApiFraudChecker apiFraudChecker;

    /**
     * 로그인 아이디 중복 체크
     */
    @GetMapping("/auth/check-login-id")
    public ApiResponse<CheckLoginIdResponse> checkLoginId(
            @RequestParam String loginId,
            HttpServletRequest request
    ) {
        apiFraudChecker.checkLoginId(request);

        boolean exists = authService.checkLoginId(loginId);
        return ApiResponse.success(new CheckLoginIdResponse(exists));
    }

    @PostMapping("/auth/register")
    public ApiResponse<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {
        User user = authService.register(request.toCommand());
        return ApiResponse.success(RegisterResponse.from(user));
    }

    @PostMapping("/auth/login")
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

    @PostMapping("/auth/logout")
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String authorization
    ) {
        // TODO: 실제 로그아웃 로직은 차후 구현
        return ApiResponse.success(null);
    }
}

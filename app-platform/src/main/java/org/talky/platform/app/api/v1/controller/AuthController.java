package org.talky.platform.app.api.v1.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talky.platform.app.api.ApiFraudChecker;
import org.talky.platform.app.api.ClientIpResolver;
import org.talky.platform.app.api.UserAgentParser;
import org.talky.platform.app.api.v1.request.LoginRequest;
import org.talky.platform.app.api.v1.request.RefreshRequest;
import org.talky.platform.app.api.v1.request.RegisterRequest;
import org.talky.platform.app.api.v1.response.CheckLoginIdResponse;
import org.talky.platform.app.api.v1.response.LoginResponse;
import org.talky.platform.app.api.v1.response.RegisterResponse;
import org.talky.platform.app.service.AuthService;
import org.talky.platform.app.vo.ClientInfo;
import org.talky.platform.app.vo.LoginResult;
import org.talky.platform.app.vo.User;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;
import org.talky.platform.support.response.ApiResponse;

@Slf4j
@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ApiFraudChecker apiFraudChecker;
    private final UserAgentParser userAgentParser;

    /**
     * 로그인 아이디 중복 체크 (회원가입 할 때 사용)
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
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpRequest
    ) {
        ClientInfo clientInfo = ClientInfo.of(
                ClientIpResolver.getClientIp(httpRequest),
                userAgentParser.parse(httpRequest.getHeader("User-Agent"))
        );

        try {
            LoginResult result = authService.login(
                    loginRequest.loginId(),
                    loginRequest.password(),
                    clientInfo
            );
            return ApiResponse.success(LoginResponse.from(result));
        } catch (Exception e) {
            log.info("[로그인 실패] loginId={}, ip={}, message={}",
                    loginRequest.loginId(), ClientIpResolver.getClientIp(httpRequest), e.getMessage());
            throw new CoreException(ErrorCode.UNAUTHORIZED);
        }
    }

    @PostMapping("/auth/logout")
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String authorization
    ) {
        String token = authorization.substring("Bearer ".length());
        authService.logout(token);
        return ApiResponse.success(null);
    }

    @PostMapping("/auth/refresh")
    public ApiResponse<LoginResponse> refresh(
            @RequestBody RefreshRequest refreshRequest,
            HttpServletRequest httpRequest
    ) {
        ClientInfo clientInfo = ClientInfo.of(
                ClientIpResolver.getClientIp(httpRequest),
                userAgentParser.parse(httpRequest.getHeader("User-Agent"))
        );

        try {
            LoginResult result = authService.refresh(
                    refreshRequest.accessToken(),
                    refreshRequest.refreshToken(),
                    clientInfo
            );
            return ApiResponse.success(LoginResponse.from(result));
        } catch (Exception e) {
            log.warn("[토큰 재발급 실패] ip={}, message={}", ClientIpResolver.getClientIp(httpRequest), e.getMessage());
            throw new CoreException(ErrorCode.UNAUTHORIZED);
        }
    }
}

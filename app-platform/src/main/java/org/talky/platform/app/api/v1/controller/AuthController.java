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
import org.talky.platform.app.api.ClientIpResolver;
import org.talky.platform.app.api.UserAgentParser;
import org.talky.platform.app.api.v1.request.LoginRequest;
import org.talky.platform.app.api.v1.request.RegisterRequest;
import org.talky.platform.app.api.v1.response.CheckLoginIdResponse;
import org.talky.platform.app.api.v1.response.LoginResponse;
import org.talky.platform.app.api.v1.response.RegisterResponse;
import org.talky.platform.app.service.AuthService;
import org.talky.platform.app.vo.ClientInfo;
import org.talky.platform.app.vo.LoginResult;
import org.talky.platform.app.vo.User;
import org.talky.platform.app.vo.UserAgentInfo;
import org.talky.platform.support.response.ApiResponse;

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
        UserAgentInfo userAgentInfo = userAgentParser.parse(httpRequest.getHeader("User-Agent"));
        ClientInfo clientInfo = ClientInfo.builder()
                .remoteIp(ClientIpResolver.getClientIp(httpRequest))
                .uaRawValue(userAgentInfo.rawValue())
                .uaOsName(userAgentInfo.osName())
                .uaDeviceName(userAgentInfo.deviceName())
                .uaAgentName(userAgentInfo.agentName())
                .uaAgentVersion(userAgentInfo.agentVersion())
                .uaDeviceClass(userAgentInfo.deviceClass())
                .build();

        LoginResult result = authService.login(
                loginRequest.loginId(),
                loginRequest.password(),
                clientInfo
        );
        return ApiResponse.success(LoginResponse.from(result));
    }

    @PostMapping("/auth/logout")
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String authorization
    ) {
        // TODO: 실제 로그아웃 로직은 차후 구현
        return ApiResponse.success(null);
    }
}

package org.talky.platform.support.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.talky.platform.app.api.ClientIpResolver;
import org.talky.auth.AccessToken;
import org.talky.auth.ExpiredTokenException;
import org.talky.auth.InvalidTokenException;
import org.talky.auth.JwtTokenProvider;
import org.talky.auth.UserRole;
import org.talky.platform.support.error.ErrorCode;
import org.talky.platform.support.response.ApiResponse;

import java.io.IOException;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        if (ApiAccessRegistry.isPublic(method, requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            sendUnauthorizedResponse(response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        AccessToken accessToken;
        try {
            accessToken = jwtTokenProvider.parseAccessToken(token);
        } catch (ExpiredTokenException e) {
            sendErrorResponse(response, ErrorCode.EXPIRED_TOKEN);
            return;
        } catch (InvalidTokenException e) {
            log.warn("[유효하지 않은 토큰] uri={}, ip={}", requestUri, ClientIpResolver.getClientIp(request));
            sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
            return;
        }

        UserRole userRole = accessToken.role();
        Set<UserRole> allowedRoles = ApiAccessRegistry.getAllowedRoles(method, requestUri);
        if (!allowedRoles.contains(userRole)) {
            log.warn("[권한 부족] uri={}, role={}, ip={}", requestUri, userRole, ClientIpResolver.getClientIp(request));
            sendForbiddenResponse(response);
            return;
        }

        request.setAttribute(AuthAttributes.USER_ID, accessToken.userId());
        request.setAttribute(AuthAttributes.USER_ROLE, userRole);
        filterChain.doFilter(request, response);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        sendErrorResponse(response, ErrorCode.UNAUTHORIZED);
    }

    private void sendForbiddenResponse(HttpServletResponse response) throws IOException {
        sendErrorResponse(response, ErrorCode.FORBIDDEN);
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> errorResponse = ApiResponse.error(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

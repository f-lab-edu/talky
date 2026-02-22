package org.talky.chat.support.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.talky.auth.AccessToken;
import org.talky.auth.ExpiredTokenException;
import org.talky.auth.InvalidTokenException;
import org.talky.auth.JwtTokenProvider;
import org.talky.auth.UserRole;
import org.talky.chat.support.error.ErrorCode;
import org.talky.chat.support.response.ApiResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationFilter implements WebFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestUri = request.getURI().getPath();
        String method = request.getMethod().name();

        if (ApiAccessRegistry.isPublic(method, requestUri)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return sendErrorResponse(exchange, ErrorCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        AccessToken accessToken;
        try {
            accessToken = jwtTokenProvider.parseAccessToken(token);
        } catch (ExpiredTokenException e) {
            return sendErrorResponse(exchange, ErrorCode.EXPIRED_TOKEN);
        } catch (InvalidTokenException e) {
            log.warn("[유효하지 않은 토큰] uri={}, token={}", requestUri, token);
            return sendErrorResponse(exchange, ErrorCode.INVALID_TOKEN);
        }

        UserRole userRole = accessToken.role();
        Set<UserRole> allowedRoles = ApiAccessRegistry.getAllowedRoles(method, requestUri);
        if (!allowedRoles.contains(userRole)) {
            log.warn("[권한 부족] uri={}, role={}", requestUri, userRole);
            return sendErrorResponse(exchange, ErrorCode.FORBIDDEN);
        }

        exchange.getAttributes().put(AuthAttributes.USER_ID, accessToken.userId());
        exchange.getAttributes().put(AuthAttributes.USER_ROLE, userRole);
        return chain.filter(exchange);
    }

    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, ErrorCode errorCode) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(errorCode.getHttpStatus());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<Void> errorResponse = ApiResponse.error(errorCode);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("[에러 응답 직렬화 실패]", e);
            return response.setComplete();
        }
    }
}

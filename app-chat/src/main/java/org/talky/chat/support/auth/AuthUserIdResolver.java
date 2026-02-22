package org.talky.chat.support.auth;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import org.talky.auth.AuthUserId;
import org.talky.chat.support.error.CoreException;
import org.talky.chat.support.error.ErrorCode;
import reactor.core.publisher.Mono;

@Component
public class AuthUserIdResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUserId.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Mono<Object> resolveArgument(
            MethodParameter parameter,
            BindingContext bindingContext,
            ServerWebExchange exchange
    ) {
        Object userId = exchange.getAttribute(AuthAttributes.USER_ID);

        if (userId == null) {
            return Mono.error(new CoreException(ErrorCode.UNAUTHORIZED));
        }

        return Mono.just(userId);
    }
}

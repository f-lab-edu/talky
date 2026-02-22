package org.talky.chat.support.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talky.auth.JwtTokenProvider;
import org.talky.chat.support.auth.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Bean
    public AuthorizationFilter authorizationFilter() {
        return new AuthorizationFilter(jwtTokenProvider, objectMapper);
    }
}

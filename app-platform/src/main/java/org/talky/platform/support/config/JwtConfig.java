package org.talky.platform.support.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talky.auth.JwtTokenProvider;

@Configuration
public class JwtConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return JwtTokenProvider.fromEnv();
    }
}

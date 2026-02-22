package org.talky.chat.support.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.talky.chat.support.auth.AuthUserIdResolver;

@Configuration
@RequiredArgsConstructor
public class WebFluxConfig implements WebFluxConfigurer {

    private final AuthUserIdResolver authUserIdResolver;

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(authUserIdResolver);
    }
}

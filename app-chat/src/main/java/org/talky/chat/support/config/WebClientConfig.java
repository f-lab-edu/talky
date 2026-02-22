package org.talky.chat.support.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${platform.base-url}")
    private String platformBaseUrl;

    @Bean
    public WebClient platformClient() {
        return WebClient.builder()
                .baseUrl(platformBaseUrl)
                .build();
    }
}

package com.rafael0117.pedido_service.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignSecurityConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            if (!requestTemplate.headers().containsKey("Authorization")) {
                return; // no token, no hacer nada
            }
            // Reenvía el header existente
            String token = requestTemplate.headers().get("Authorization")
                    .stream()
                    .findFirst()
                    .orElse(null);
            if (token != null) {
                requestTemplate.header("Authorization", token);
            }
        };
    }
}

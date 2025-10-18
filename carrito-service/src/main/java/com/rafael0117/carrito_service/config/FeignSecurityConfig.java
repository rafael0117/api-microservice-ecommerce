package com.rafael0117.carrito_service.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignSecurityConfig {
    @Bean
    RequestInterceptor bearerTokenInterceptor(){
        return template -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwt){
                template.header
                        ("Authorization", "Bearer "
                                + jwt.getToken().getTokenValue());
            }
        };
    }
}

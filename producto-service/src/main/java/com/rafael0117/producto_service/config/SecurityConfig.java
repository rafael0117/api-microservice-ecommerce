package com.rafael0117.producto_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,"/api/categorias/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/categorias/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/productos/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/productos/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/api/productos/**")
                        .permitAll()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(Customizer.withDefaults()));
        return httpSecurity.build();
    }
}
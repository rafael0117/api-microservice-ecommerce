package com.rafael0117.pedido_service.config;

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
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll() // 👈 IMPORTANTE
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
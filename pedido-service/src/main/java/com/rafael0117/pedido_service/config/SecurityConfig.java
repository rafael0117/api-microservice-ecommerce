package com.rafael0117.pedido_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity)throws Exception{
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**")
                        .hasAuthority("SCOPE_pedido.read")
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/**")
                        .hasAuthority("SCOPE_pedido.write")
                )

                .oauth2ResourceServer(oauth ->
                        oauth.jwt(Customizer.withDefaults()));
        return httpSecurity.build();
    }
}

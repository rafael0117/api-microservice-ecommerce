package com.rafael0117.producto_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,"/api/categorias/**")
                        .hasAuthority("SCOPE_categoria.write")
                        .requestMatchers(HttpMethod.GET,"/api/categorias/**")
                        .hasAuthority("SCOPE_categoria.read")
                        .requestMatchers(HttpMethod.GET, "/api/productos/**")
                        .hasAuthority("SCOPE_producto.read")
                        .requestMatchers(HttpMethod.POST,"/api/productos/**")
                        .hasAuthority("SCOPE_producto.write")
                )

                .oauth2ResourceServer(oauth ->
                        oauth.jwt(Customizer.withDefaults()));
        return httpSecurity.build();
    }
}

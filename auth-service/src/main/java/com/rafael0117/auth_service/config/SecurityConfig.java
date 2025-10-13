package com.rafael0117.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🔒 Desactivar CSRF (no se usa en APIs REST)
                .csrf(AbstractHttpConfigurer::disable)

                // 🔐 Configuración de rutas y permisos
                .authorizeHttpRequests(auth -> auth
                        // 👇 Permitir libremente las rutas de autenticación
                        .requestMatchers("/api/auth/**").permitAll()

                        // 👇 (Ejemplo) proteger endpoints de usuario
                        .requestMatchers(HttpMethod.GET, "/api/auth/**").hasAuthority("SCOPE_auth.read")
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").hasAuthority("SCOPE_auth.write")

                        // 🔒 Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // 🔑 Habilitar autenticación por tokens JWT
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

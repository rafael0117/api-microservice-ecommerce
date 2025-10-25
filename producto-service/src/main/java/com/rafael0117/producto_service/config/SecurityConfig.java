package com.rafael0117.producto_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableMethodSecurity // para @PreAuthorize
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(req -> {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowCredentials(true);
                    cfg.setAllowedOrigins(List.of(System.getProperty(
                            "cors.allowed-origins",
                            System.getenv().getOrDefault("CORS_ALLOWED_ORIGINS", "http://localhost:4200")
                    )));
                    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    return cfg;
                }))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // públicos
                        .requestMatchers(
                                "/actuator/**",
                                "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()

                        // ejemplo: GET públicos de listado/detalle
                        .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias/**").authenticated()
                        // lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthConverter())
                        )
                );

        return http.build();
    }

    /**
     * Decoder HS256 con secret compartido con el auth-service
     */
    @Bean
    JwtDecoder jwtDecoder() {
        SecretKey key = new javax.crypto.spec.SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        return decoder;
    }

    /**
     * Extrae roles del token.
     * - Usa "roles" o "authorities" si existen.
     * - Asegura prefijo ROLE_ cuando falte.
     */
    @Bean
    JwtAuthenticationConverter jwtAuthConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        scopesConverter.setAuthorityPrefix("ROLE_");
        scopesConverter.setAuthoritiesClaimName("roles"); // o "authorities" según tu token

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // roles
            Collection<GrantedAuthority> authorities = new ArrayList<>(scopesConverter.convert(jwt));

            Object claimRoles = jwt.getClaims().get("roles");
            if (claimRoles instanceof List<?> list) {
                list.forEach(r -> authorities.add(new SimpleGrantedAuthority(
                        r.toString().startsWith("ROLE_") ? r.toString() : "ROLE_" + r
                )));
            }

            Object claimAuth = jwt.getClaims().get("authorities");
            if (claimAuth instanceof List<?> list) {
                list.forEach(a -> authorities.add(new SimpleGrantedAuthority(
                        a.toString().startsWith("ROLE_") ? a.toString() : "ROLE_" + a
                )));
            }

            return authorities;
        });

        return jwtConverter;
    }
}
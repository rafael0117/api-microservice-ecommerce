package com.rafael0117.auth_service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rafael0117.auth_service.domain.model.Usuario;
import com.rafael0117.auth_service.domain.repository.UsuarioRepository;
import com.rafael0117.auth_service.application.service.UsuarioService;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UserDetailsService, UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol())
                .build();
    }

    @Override
    public String login(String username, String password) {
        try {
            // ✅ Obtenemos el AuthenticationManager de forma dinámica
            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            if (authentication.isAuthenticated()) {
                return "Login exitoso para el usuario: " + username;
            } else {
                return "Credenciales inválidas";
            }

        } catch (AuthenticationException e) {
            return "Error en autenticación: " + e.getMessage();
        } catch (Exception e) {
            return "Error al obtener AuthenticationManager: " + e.getMessage();
        }
    }


    @Override
    public Usuario registrar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }
}

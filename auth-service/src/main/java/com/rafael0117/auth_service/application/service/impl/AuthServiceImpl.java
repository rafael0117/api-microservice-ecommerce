package com.rafael0117.auth_service.application.service.impl;


import com.rafael0117.auth_service.application.mapper.UsuarioMapper;
import com.rafael0117.auth_service.application.service.AuthService;
import com.rafael0117.auth_service.domain.model.RefreshToken;
import com.rafael0117.auth_service.domain.model.Role;
import com.rafael0117.auth_service.domain.model.User;
import com.rafael0117.auth_service.domain.repository.RefreshTokenRepository;
import com.rafael0117.auth_service.domain.repository.RoleRepository;
import com.rafael0117.auth_service.domain.repository.UserRepository;
import com.rafael0117.auth_service.security.util.JwtUtil;
import com.rafael0117.auth_service.web.dto.RegistrarRequestDTO;
import com.rafael0117.auth_service.web.dto.TokenPairResponse;
import com.rafael0117.auth_service.web.dto.UsuarioDTO;
import com.rafael0117.auth_service.web.dto.login.LoginRequest;
import com.rafael0117.auth_service.web.dto.login.LoginResponse;
import io.jsonwebtoken.Jwt;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RoleRepository rolRepository;
    private final UserRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse authenticateLegacy(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword())
        );
        UserDetails user = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        String access = jwtUtil.generateAccessToken(user);
        long accessExp = jwtUtil.extractExpiration(access).getTime();

        return LoginResponse.builder()
                .token(access)
                .username(user.getUsername())
                .roles(user.getAuthorities().stream().map(a -> a.getAuthority()).toList())
                .expirateAt(accessExp)
                .build();
    }

    @Override
    public TokenPairResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword())
        );
        UserDetails user = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        String access = jwtUtil.generateAccessToken
                (user);
        String refresh = jwtUtil.generateRefreshToken(user);

        User u = usuarioRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        refreshTokenRepository.save(RefreshToken.builder()
                .token(refresh)
                .user(u)
                .expiresAt(jwtUtil.extractExpiration(refresh).toInstant())
                .revoked(false)
                .build());

        return TokenPairResponse.builder()
                .username(user.getUsername())
                .roles(user.getAuthorities().stream().map(a -> a.getAuthority()).toList())
                .accessToken(access)
                .accessExpAt(jwtUtil.extractExpiration(access).getTime())
                .refreshToken(refresh)
                .refreshExpAt(jwtUtil.extractExpiration(refresh).getTime())
                .build();
    }

    @Override
    public TokenPairResponse refresh(String refreshToken) {
        var stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token inválido"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now()))
            throw new RuntimeException("Refresh token expirado o revocado");

        if (!jwtUtil.validateToken(refreshToken) || !"refresh".equals(jwtUtil.extractType(refreshToken)))
            throw new RuntimeException("Token no es refresh");

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(username);

        // rotación segura
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        String newAccess = jwtUtil.generateAccessToken(user);
        String newRefresh = jwtUtil.generateRefreshToken(user);

        refreshTokenRepository.save(RefreshToken.builder()
                .token(newRefresh)
                .user(stored.getUser())
                .expiresAt(jwtUtil.extractExpiration(newRefresh).toInstant())
                .revoked(false)
                .build());

        return TokenPairResponse.builder()
                .username(username)
                .roles(user.getAuthorities().stream().map(a -> a.getAuthority()).toList())
                .accessToken(newAccess)
                .accessExpAt(jwtUtil.extractExpiration(newAccess).getTime())
                .refreshToken(newRefresh)
                .refreshExpAt(jwtUtil.extractExpiration(newRefresh).getTime())
                .build();
    }

    @Override
    @Transactional
    public void logoutAllForUser(String username) {
        var user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        refreshTokenRepository.deleteAllByUserId(user.getId());
    }

    @Override
    public String register(RegistrarRequestDTO request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El usuario ya existe");
        }
        Role rolUser = rolRepository.findById("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("El rol USER no existe"));

        User usuario = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(rolUser))
                .build();

        usuarioRepository.save(usuario);
        return "Usuario registrado exitosamente";
    }

    @Transactional
    @Override
    public UsuarioDTO buscarPorCodigo(Long id) {
        return usuarioRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Usuario " + id + " no existe"));
    }

    @Override
    public UsuarioDTO me(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Falta Authorization Bearer");
        }
        String token = authorizationHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token inválido/expirado");
        }
        if (!"access".equals(jwtUtil.extractType(token))) {
            throw new RuntimeException("Se requiere access token");
        }

        String username = jwtUtil.extractUsername(token);
        var user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapper.toDto(user);
    }
}
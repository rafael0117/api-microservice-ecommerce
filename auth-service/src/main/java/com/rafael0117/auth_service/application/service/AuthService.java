package com.rafael0117.auth_service.application.service;

import com.rafael0117.auth_service.web.dto.RegistrarRequestDTO;
import com.rafael0117.auth_service.web.dto.TokenPairResponse;
import com.rafael0117.auth_service.web.dto.UsuarioDTO;
import com.rafael0117.auth_service.web.dto.login.LoginRequest;
import com.rafael0117.auth_service.web.dto.login.LoginResponse;
import io.jsonwebtoken.Jwt;

public interface AuthService {
    // accesos
    LoginResponse authenticateLegacy(LoginRequest loginRequest); // opcional (legacy)
    TokenPairResponse login(LoginRequest loginRequest);
    TokenPairResponse refresh(String refreshToken);
    void logoutAllForUser(String username);

    // registro
    String register(RegistrarRequestDTO registerRequest);
    UsuarioDTO buscarPorCodigo(Long id);
    UsuarioDTO me(String authorizationHeader);
}
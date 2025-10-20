package com.rafael0117.auth_service.web.controller;


import com.rafael0117.auth_service.application.service.AuthService;
import com.rafael0117.auth_service.web.dto.RegistrarRequestDTO;
import com.rafael0117.auth_service.web.dto.TokenPairResponse;
import com.rafael0117.auth_service.web.dto.UsuarioDTO;
import com.rafael0117.auth_service.web.dto.login.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<TokenPairResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(service.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPairResponse> refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String refreshToken = authHeader.substring(7);
        return ResponseEntity.ok(service.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestParam String username) {
        service.logoutAllForUser(username);
        return ResponseEntity.ok(Map.of("message","Sesiones cerradas (refresh tokens borrados)"));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegistrarRequestDTO request) {
        service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Usuario registrado exitosamente"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorCodigo(@PathVariable Long id){
        return ResponseEntity.ok(service.buscarPorCodigo(id));
    }


    @GetMapping("/me")
    public UsuarioDTO me(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return service.me(authorization);
    }
}
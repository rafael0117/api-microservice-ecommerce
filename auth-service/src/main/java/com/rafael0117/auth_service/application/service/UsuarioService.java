package com.rafael0117.auth_service.application.service;

import com.rafael0117.auth_service.domain.model.Usuario;

public interface UsuarioService {
    Usuario registrar(Usuario usuario);
    String login(String username, String password);
}

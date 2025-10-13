package com.rafael0117.auth_service.domain.repository;

import com.rafael0117.auth_service.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByUsername(String username);
}

package com.rafael0117.carrito_service.domain.repository;

import com.rafael0117.carrito_service.domain.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito,Long> {
    Optional<Carrito> findByUsuarioId(Long usuarioId);
}

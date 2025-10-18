package com.rafael0117.carrito_service.domain.repository;

import com.rafael0117.carrito_service.domain.model.DetalleCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Long> {
    DetalleCarrito findByIdProducto(Long idProducto);
}
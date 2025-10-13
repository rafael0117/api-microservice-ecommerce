package com.rafael0117.carrito_service.domain.repository;

import com.rafael0117.carrito_service.domain.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito,Long> {
}

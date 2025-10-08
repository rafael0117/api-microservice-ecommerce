package com.rafael0117.producto_service.domain.repository;

import com.rafael0117.producto_service.domain.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto,Long> {
}

package com.rafael0117.producto_service.domain.repository;

import com.rafael0117.producto_service.domain.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria,Long> {
}

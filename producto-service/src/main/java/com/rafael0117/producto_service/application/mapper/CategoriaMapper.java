package com.rafael0117.producto_service.application.mapper;

import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaRequestDto;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaResponseDto;

public interface CategoriaMapper {
    Categoria toDomain(CategoriaRequestDto categoriaRequestDto);
    CategoriaResponseDto toDto(Categoria categoria);
}

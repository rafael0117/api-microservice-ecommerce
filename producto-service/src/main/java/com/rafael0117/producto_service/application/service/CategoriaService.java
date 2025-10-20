package com.rafael0117.producto_service.application.service;

import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaRequestDto;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaResponseDto;

import java.util.List;

public interface CategoriaService {
    List<CategoriaResponseDto> listar();
    CategoriaResponseDto guardar(CategoriaRequestDto categoriaRequestDto);
    CategoriaResponseDto actualizar(Long id, CategoriaRequestDto categoriaRequestDto);
}

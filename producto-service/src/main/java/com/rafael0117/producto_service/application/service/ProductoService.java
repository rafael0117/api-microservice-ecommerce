package com.rafael0117.producto_service.application.service;

import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;

import java.util.List;

public interface ProductoService {
    List<ProductoResponseDto> listar();
    ProductoResponseDto guardar(ProductoRequestDto productoRequestDto);
}

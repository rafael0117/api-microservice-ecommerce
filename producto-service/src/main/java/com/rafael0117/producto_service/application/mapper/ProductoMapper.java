package com.rafael0117.producto_service.application.mapper;


import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.domain.model.Producto;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;

public interface ProductoMapper {
    Producto toDomain(ProductoRequestDto productoRequestDto);
    ProductoResponseDto toDto(Producto producto);
}

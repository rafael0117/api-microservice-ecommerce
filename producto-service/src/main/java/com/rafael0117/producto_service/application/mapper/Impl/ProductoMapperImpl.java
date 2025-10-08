package com.rafael0117.producto_service.application.mapper.Impl;

import com.rafael0117.producto_service.application.mapper.ProductoMapper;
import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.domain.model.Producto;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapperImpl implements ProductoMapper {


    @Override
    public Producto toDomain(ProductoRequestDto productoRequestDto, Categoria categoria) {
        if (productoRequestDto == null) {
            return null;
        }

        return Producto.builder()
                .nombre(productoRequestDto.getNombre())
                .descripcion(productoRequestDto.getDescripcion())
                .precio(productoRequestDto.getPrecio())
                .stock(productoRequestDto.getStock())
                .talla(productoRequestDto.getTalla())
                .color(productoRequestDto.getColor())
                .imagen(productoRequestDto.getImagen())
                .categoria(categoria)
                .build();
    }

    @Override
    public ProductoResponseDto toDto(Producto producto) {
        if (producto == null) {
            return null;
        }

        return ProductoResponseDto.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .talla(producto.getTalla())
                .color(producto.getColor())
                .imagen(producto.getImagen())
                .categoriaNombre(
                        producto.getCategoria() != null ? producto.getCategoria().getNombre() : null
                )
                .build();
    }
}

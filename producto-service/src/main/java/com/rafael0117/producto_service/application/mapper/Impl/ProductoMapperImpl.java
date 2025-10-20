package com.rafael0117.producto_service.application.mapper.Impl;

import com.rafael0117.producto_service.application.mapper.ProductoMapper;
import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.domain.model.Producto;
import com.rafael0117.producto_service.domain.model.ProductoImagen;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ProductoMapperImpl implements ProductoMapper {


    @Override
    public Producto toDomain(ProductoRequestDto productoRequestDto) {
        if (productoRequestDto == null) return null;
        return Producto.builder()
                .nombre(productoRequestDto.getNombre())
                .descripcion(productoRequestDto.getDescripcion())
                .precio(productoRequestDto.getPrecio())
                .stock(productoRequestDto.getStock())
                .talla(productoRequestDto.getTalla())
                .color(productoRequestDto.getColor())
                .categoriaId(productoRequestDto.getCategoriaId())
                .build();
    }

    @Override
    public ProductoResponseDto toDto(Producto p) {
        if (p == null) return null;

        List<String> imgs = (p.getImagenes() == null) ? List.of()
                : p.getImagenes().stream()
                .sorted(Comparator.comparingInt(pi -> pi.getOrden() == null ? 0 : pi.getOrden()))
                .map(ProductoImagen::getBase64)
                .toList();

        return ProductoResponseDto.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precio(p.getPrecio())
                .stock(p.getStock())
                .talla(p.getTalla())
                .color(p.getColor())
                .categoriaId(p.getCategoriaId())
                .imagenesBase64(imgs)
                .build();
    }

}


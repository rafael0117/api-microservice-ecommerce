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
        if (productoRequestDto == null) {
            return null;
        }

        return Producto.builder()
                .nombre(productoRequestDto.getNombre())
                .descripcion(productoRequestDto.getDescripcion())
                .precio(productoRequestDto.getPrecio())
                .stock(productoRequestDto.getStock())
                .reservado(productoRequestDto.getReservado())
                .talla(productoRequestDto.getTalla())
                .color(productoRequestDto.getColor())
                .categoriaId(productoRequestDto.getCategoriaId())
                .build();
    }

    @Override
    public ProductoResponseDto toDto(Producto producto) {
        if (producto == null) return null;

        List<String> urls = producto.getImagenes() == null ? List.of()
                : producto.getImagenes().stream()
                .sorted(Comparator.comparingInt(ProductoImagen::getOrden))
                .map(ProductoImagen::getUrl)
                .toList();

        return ProductoResponseDto.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .reservado(producto.getReservado())
                .talla(producto.getTalla())
                .color(producto.getColor())
                .categoriaId(producto.getCategoriaId())
                .imagenesUrl(urls) // 👈 múltiples imágenes
                .build();
    }

}


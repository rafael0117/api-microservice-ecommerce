package com.rafael0117.producto_service.application.mapper.Impl;

import com.rafael0117.producto_service.application.mapper.ProductoMapper;
import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.domain.model.Producto;
import com.rafael0117.producto_service.domain.model.ProductoImagen;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class ProductoMapperImpl implements ProductoMapper {


    @Override
    public Producto toDomain(ProductoRequestDto d) {
        if (d == null) return null;

        return Producto.builder()


                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .precio(d.getPrecio())
                .stock(d.getStock())
                .tallas(new ArrayList<>(Optional.ofNullable(d.getTalla()).orElse(List.of())))
                .colores(new ArrayList<>(Optional.ofNullable(d.getColor()).orElse(List.of())))
                .categoriaId(d.getCategoriaId())

                .build();
    }

    @Override
    public ProductoResponseDto toDto(Producto p) {
        if (p == null) return null;

        // Ordena por 'orden' (null -> 0) y devuelve el base64 tal como lo usabas
        List<String> imgs = (p.getImagenesBase64() == null) ? List.of()
                : p.getImagenesBase64().stream()
                .sorted(Comparator.comparingInt(pi -> pi.getOrden() == null ? 0 : pi.getOrden()))
                .map(ProductoImagen::getBase64)
                .toList();

        return ProductoResponseDto.builder()


                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precio(p.getPrecio())
                .stock(p.getStock())
                // 👇 único ajuste por el cambio a listas en la entidad
                .talla(p.getTallas())     // antes p.getTalla()
                .color(p.getColores())    // antes p.getColor()
                .categoriaId(p.getCategoriaId())
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .imagenesBase64(imgs)     // se mantiene igual

                .build();
    }

}


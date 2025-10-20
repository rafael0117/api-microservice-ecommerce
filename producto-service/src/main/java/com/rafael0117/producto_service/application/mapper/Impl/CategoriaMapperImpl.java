package com.rafael0117.producto_service.application.mapper.Impl;

import com.rafael0117.producto_service.application.mapper.CategoriaMapper;
import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaRequestDto;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapperImpl implements CategoriaMapper {
    @Override
    public Categoria toDomain(CategoriaRequestDto categoriaRequestDto) {
        if (categoriaRequestDto == null) {
            return null;
        }

        return Categoria.builder()
                .nombre(categoriaRequestDto.getNombre())
                .estado(categoriaRequestDto.getEstado())
                .build();
    }

    @Override
    public CategoriaResponseDto toDto(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        return CategoriaResponseDto.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .estado(categoria.getEstado())
                .build();
    }
}

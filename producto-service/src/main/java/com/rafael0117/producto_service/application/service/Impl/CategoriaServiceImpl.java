package com.rafael0117.producto_service.application.service.Impl;

import com.rafael0117.producto_service.application.mapper.CategoriaMapper;
import com.rafael0117.producto_service.application.service.CategoriaService;
import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.domain.repository.CategoriaRepository;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaRequestDto;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    public List<CategoriaResponseDto> listar() {
        return categoriaRepository.findAll().stream().map(categoriaMapper::toDto).toList();
    }

    @Override
    public CategoriaResponseDto guardar(CategoriaRequestDto categoriaRequestDto) {
        Categoria categoria = categoriaMapper.toDomain(categoriaRequestDto);
        Categoria categoriaRegistrada = categoriaRepository.save(categoria);
        return categoriaMapper.toDto(categoriaRegistrada);
    }

    @Override
    public CategoriaResponseDto actualizar(Long id, CategoriaRequestDto dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

        if (dto.getNombre() != null) {
            categoria.setNombre(dto.getNombre());
        }

        if (dto.getEstado() != null) {
            categoria.setEstado(dto.getEstado());
        }

        Categoria actualizada = categoriaRepository.save(categoria);
        return categoriaMapper.toDto(actualizada);
    }
}

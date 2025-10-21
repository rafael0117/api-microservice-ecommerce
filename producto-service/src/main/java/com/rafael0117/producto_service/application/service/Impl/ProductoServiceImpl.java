package com.rafael0117.producto_service.application.service.Impl;


import com.rafael0117.producto_service.application.mapper.ProductoMapper;
import com.rafael0117.producto_service.application.service.ProductoService;

import com.rafael0117.producto_service.domain.model.Producto;
import com.rafael0117.producto_service.domain.model.ProductoImagen;

import com.rafael0117.producto_service.domain.repository.CategoriaRepository;
import com.rafael0117.producto_service.domain.repository.ProductoImagenRepository;
import com.rafael0117.producto_service.domain.repository.ProductoRepository;

import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;
    private final ProductoImagenRepository imagenRepository;


    private String baseUrl() { return "/uploads/"; }


    @Override
    public List<ProductoResponseDto> listar() {
        return productoRepository.findAll().stream().map(productoMapper::toDto).toList();
    }

    @Override
    public ProductoResponseDto guardar(ProductoRequestDto dto) {
        // 1) mapear y guardar el producto
        Producto producto = productoMapper.toDomain(dto);

        // 2) agregar imágenes base64 al entity
        if (dto.getImagenesBase64() != null && !dto.getImagenesBase64().isEmpty()) {
            int i = 0;
            for (String b64 : dto.getImagenesBase64()) {
                if (b64 == null || b64.isBlank()) continue;
                ProductoImagen img = ProductoImagen.builder()
                        .base64(b64.trim())
                        .orden(i++)
                        .producto(producto)
                        .build();
                producto.getImagenesBase64().add(img);
            }
        }

        // 3) guardar (cascade ALL en Producto -> ProductoImagen lo persiste todo)
        producto = productoRepository.save(producto);

        return productoMapper.toDto(producto);
    }

    @Transactional
    @Override
    public ProductoResponseDto actualizar(Long id, ProductoRequestDto dto) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        if (!categoriaRepository.existsById(dto.getCategoriaId())) {
            throw new IllegalArgumentException("Categoría no existe");
        }

        // validaciones básicas (opcional)
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new IllegalArgumentException("Nombre requerido");
        if (dto.getPrecio() == null || dto.getPrecio() < 0)
            throw new IllegalArgumentException("Precio inválido");
        if (dto.getStock() == null || dto.getStock() < 0)
            throw new IllegalArgumentException("Stock inválido");


        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setStock(dto.getStock());
        p.setCategoriaId(dto.getCategoriaId());

        p.getTallas().clear();
        p.getTallas().addAll(Optional.ofNullable(dto.getTalla()).orElse(List.of()));

        p.getColores().clear();
        p.getColores().addAll(Optional.ofNullable(dto.getColor()).orElse(List.of()));

        // (opcional) si deseas que PUT reemplace imágenes cuando vengan
        if (dto.getImagenesBase64() != null) {
            p.getImagenesBase64().clear();
            int i = 0;
            for (String b64 : dto.getImagenesBase64()) {
                p.getImagenesBase64().add(
                        ProductoImagen.builder()
                                .orden(i++)
                                .base64(b64)
                                .producto(p)
                                .build()
                );
            }
        }

        p = productoRepository.save(p);
        return productoMapper.toDto(p);
    }

    @Override
    public ProductoResponseDto buscarPorId(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el Id " + id));
        return productoMapper.toDto(p);
    }


    @Override
    public void eliminar(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Producto no encontrado con id: " + id);
        }
    }

}

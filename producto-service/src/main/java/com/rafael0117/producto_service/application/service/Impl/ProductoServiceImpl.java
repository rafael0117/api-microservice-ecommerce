package com.rafael0117.producto_service.application.service.Impl;


import com.rafael0117.producto_service.application.mapper.ProductoMapper;
import com.rafael0117.producto_service.application.service.ProductoService;

import com.rafael0117.producto_service.domain.model.Producto;
import com.rafael0117.producto_service.domain.model.ProductoImagen;

import com.rafael0117.producto_service.domain.repository.ProductoImagenRepository;
import com.rafael0117.producto_service.domain.repository.ProductoRepository;

import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
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
                producto.getImagenes().add(img);
            }
        }

        // 3) guardar (cascade ALL en Producto -> ProductoImagen lo persiste todo)
        producto = productoRepository.save(producto);

        return productoMapper.toDto(producto);
    }

    @Override
    public ProductoResponseDto buscarPorId(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el Id " + id));
        return productoMapper.toDto(p);
    }


    @Override
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id); // por orphanRemoval=true se borran imágenes asociadas
    }

}

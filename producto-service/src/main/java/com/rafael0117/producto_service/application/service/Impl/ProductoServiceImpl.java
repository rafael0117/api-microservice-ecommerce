package com.rafael0117.producto_service.application.service.Impl;


import com.rafael0117.producto_service.application.mapper.ProductoMapper;
import com.rafael0117.producto_service.application.service.ImageStorage;
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
    private final ImageStorage storage;

    private String baseUrl() { return "/uploads/"; }


    @Override
    public List<ProductoResponseDto> listar() {
        return productoRepository.findAll().stream().map(productoMapper::toDto).toList();
    }

    @Override
    public ProductoResponseDto guardar(ProductoRequestDto dto, List<MultipartFile> imagenes) {
        Producto p = productoMapper.toDomain(dto);
        p = productoRepository.save(p);

        if (imagenes != null && !imagenes.isEmpty()) {
            int i = 0;
            List<ProductoImagen> imgs = new ArrayList<>();
            for (MultipartFile f : imagenes) {
                String name = storage.save(f);
                imgs.add(ProductoImagen.builder()
                        .nombreArchivo(name)
                        .url("/uploads/" + name)
                        .orden(i++)
                        .producto(p)
                        .build());
            }
            p.getImagenes().addAll(imgs);
            imagenRepository.saveAll(imgs);
        }

        return productoMapper.toDto(p);
    }

    @Override
    public ProductoResponseDto buscarPorId(Long id) {
        return productoRepository.findById(id).map(productoMapper::toDto)
                .orElseThrow(()->new RuntimeException("No se encontro el Id"));
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

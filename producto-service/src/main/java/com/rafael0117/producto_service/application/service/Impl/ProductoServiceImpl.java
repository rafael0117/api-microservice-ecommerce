package com.rafael0117.producto_service.application.service.Impl;


import com.rafael0117.producto_service.application.mapper.ProductoMapper;
import com.rafael0117.producto_service.application.service.ImageStorage;
import com.rafael0117.producto_service.application.service.ProductoService;

import com.rafael0117.producto_service.domain.model.Producto;
import com.rafael0117.producto_service.domain.model.ProductoImagen;

import com.rafael0117.producto_service.domain.repository.ProductoImagenRepository;
import com.rafael0117.producto_service.domain.repository.ProductoRepository;

import com.rafael0117.producto_service.exception.StockInsuficienteException;



import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

    private final CategoriaRepository categoriaRepository;

    private final ProductoImagenRepository imagenRepository;
    private final ImageStorage storage;

    private String baseUrl() { return "/uploads/"; }


    @Override
    public List<ProductoResponseDto> listar() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toDto)
                .toList();
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
        return productoRepository.findById(id)
                .map(productoMapper::toDto)
                .orElseThrow(() -> new RuntimeException("No se encontró el Id"));
    }

    @Override
    public void eliminar(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Producto no encontrado con id: " + id);
        }
    }

    // ==========================
    // OPERACIONES DE STOCK
    // ==========================

    /** Reserva: mueve disponibles -> reservado (no consume stock total) */
    @Override
    @Transactional
    public void reservar(Long id, int cantidad) {
        if (cantidad <= 0) return;

        // CARGA ENTIDAD (no DTO) para modificar y persistir
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));

        int disponibles = p.getStock() - safe(p.getReservado());
        if (disponibles < cantidad) {
            throw new StockInsuficienteException("No hay stock disponible para reservar. Disponible: " + disponibles);
        }

        p.setReservado(safe(p.getReservado()) + cantidad);
        productoRepository.save(p); // @Version maneja concurrencia optimista
    }

    /** Descontar: consumo definitivo. Preferir descontar desde lo reservado */
    @Override
    @Transactional
    public void descontar(Long id, int cantidad) {
        if (cantidad <= 0) return;

        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));

        int reservado = safe(p.getReservado());
        int aDescontarDesdeReservado = Math.min(cantidad, reservado);
        p.setReservado(reservado - aDescontarDesdeReservado);

        int restante = cantidad - aDescontarDesdeReservado;
        if (restante > 0) {
            int disponibles = p.getStock() - p.getReservado();
            if (disponibles < restante) {
                throw new StockInsuficienteException("Stock insuficiente para descontar. Falta: " + restante);
            }
        }

        p.setStock(p.getStock() - cantidad);
        if (p.getStock() < 0) {
            throw new IllegalStateException("Stock quedó negativo (inconsistencia).");
        }

        productoRepository.save(p);
    }

    /** Libera: devuelve reservas a disponibles */
    @Override
    @Transactional
    public void liberar(Long id, int cantidad) {
        if (cantidad <= 0) return;
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));
        int reservado = p.getReservado() == null ? 0 : p.getReservado();
        int liberar = Math.min(cantidad, reservado);
        if (liberar == 0) return;               // nada que cambiar
        p.setReservado(reservado - liberar);
        productoRepository.save(p);             // <- fuerza persistencia si cambió
    }


    private int safe(Integer v) { return v == null ? 0 : v; }
}

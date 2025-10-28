package com.rafael0117.producto_service.application.service.Impl;

import com.rafael0117.producto_service.application.mapper.ProductoMapper;
import com.rafael0117.producto_service.application.service.ProductoService;
import com.rafael0117.producto_service.domain.model.Producto;
import com.rafael0117.producto_service.domain.model.ProductoImagen;
import com.rafael0117.producto_service.domain.repository.CategoriaRepository;
import com.rafael0117.producto_service.domain.repository.ProductoRepository;
import com.rafael0117.producto_service.exception.StockInsuficienteException;
import com.rafael0117.producto_service.kafka.ProductEventsProducer;
import com.rafael0117.producto_service.kafka.events.ProductChangedPayload;
import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;
    private final ProductEventsProducer productEventsProducer;

    // ==========================
    // CRUD
    // ==========================

    @Override
    public List<ProductoResponseDto> listar() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toDto)
                .toList();
    }

    @Override
    public ProductoResponseDto guardar(ProductoRequestDto dto) {
        // Mapear a entidad
        Producto producto = productoMapper.toDomain(dto);

        // Imágenes base64
        if (dto.getImagenesBase64() != null && !dto.getImagenesBase64().isEmpty()) {
            int i = 0;
            for (String b64 : dto.getImagenesBase64()) {
                if (b64 == null || b64.isBlank()) continue;
                producto.getImagenesBase64().add(
                        ProductoImagen.builder()
                                .base64(b64.trim())
                                .orden(i++)
                                .producto(producto)
                                .build()
                );
            }
        }

        // Persistir
        producto = productoRepository.save(producto);

        // Evento CREATED
        emitChange("CREATED", producto);

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

        // Validaciones
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new IllegalArgumentException("Nombre requerido");
        if (dto.getPrecio() == null || dto.getPrecio().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Precio inválido");
        if (dto.getStock() == null || dto.getStock() < 0)
            throw new IllegalArgumentException("Stock inválido");

        // Actualizar datos
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setStock(dto.getStock());
        p.setCategoriaId(dto.getCategoriaId());
        p.setCategoriaSexo(dto.getCategoriaSexo());

        p.getTallas().clear();
        p.getTallas().addAll(Optional.ofNullable(dto.getTalla()).orElse(List.of()));

        p.getColores().clear();
        p.getColores().addAll(Optional.ofNullable(dto.getColor()).orElse(List.of()));

        // Reemplazar imágenes (si vienen)
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

        // Persistir
        p = productoRepository.save(p);

        // Evento UPDATED
        emitChange("UPDATED", p);

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
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));

        productoRepository.delete(p);

        // Evento DELETED
        emitChange("DELETED", p);
    }

    // ==========================
    // STOCK
    // ==========================

    /** Reserva: mueve disponibles -> reservado (no consume stock total) */
    @Override
    @Transactional
    public void reservar(Long id, int cantidad) {
        if (cantidad <= 0) return;

        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));

        int disponibles = p.getStock() - safe(p.getReservado());
        if (disponibles < cantidad) {
            throw new StockInsuficienteException("No hay stock disponible para reservar. Disponible: " + disponibles);
        }

        p.setReservado(safe(p.getReservado()) + cantidad);
        productoRepository.save(p);

        // Evento STOCK_RESERVED
        emitChange("STOCK_RESERVED", p);
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

        // Evento STOCK_DEDUCTED
        emitChange("STOCK_DEDUCTED", p);
    }

    /** Libera: devuelve reservas a disponibles */
    @Override
    @Transactional
    public void liberar(Long id, int cantidad) {
        if (cantidad <= 0) return;

        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));

        int reservado = safe(p.getReservado());
        int liberar = Math.min(cantidad, reservado);
        if (liberar == 0) return;

        p.setReservado(reservado - liberar);
        productoRepository.save(p);

        // Evento STOCK_RELEASED
        emitChange("STOCK_RELEASED", p);
    }

    // ==========================
    // Helpers
    // ==========================

    private void emitChange(String changeType, Producto p) {
        productEventsProducer.publishProductChanged(
                ProductChangedPayload.builder()
                        .id(p.getId())
                        .nombre(p.getNombre())
                        .precio(p.getPrecio())
                        .stock(p.getStock())
                        .tallas(p.getTallas())
                        .colores(p.getColores())
                        .categoriaId(p.getCategoriaId())
                        .reservado(p.getReservado())
                        .changeType(changeType)
                        .build()
        );
    }

    private int safe(Integer v) { return v == null ? 0 : v; }
}

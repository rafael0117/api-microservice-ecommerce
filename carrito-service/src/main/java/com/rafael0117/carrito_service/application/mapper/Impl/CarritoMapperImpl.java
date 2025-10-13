package com.rafael0117.carrito_service.application.mapper.Impl;

import com.rafael0117.carrito_service.application.mapper.CarritoMapper;
import com.rafael0117.carrito_service.domain.model.Carrito;
import com.rafael0117.carrito_service.domain.model.ItemCarrito;
import com.rafael0117.carrito_service.web.dto.carrito.CarritoRequestDto;
import com.rafael0117.carrito_service.web.dto.carrito.CarritoResponseDto;
import com.rafael0117.carrito_service.web.dto.carrito.ItemCarritoDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class CarritoMapperImpl implements CarritoMapper {
    @Override
    public Carrito toDomain(CarritoRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return Carrito.builder()
                .usuarioId(1L)
                .items(dto.getItems() != null
                        ? dto.getItems().stream()
                        .map(item -> ItemCarrito.builder()
                                .productoId(item.getProductoId())
                                .cantidad(item.getCantidad())
                                .build())
                        .collect(Collectors.toList())
                        : null)
                .build();
    }

    @Override
    public CarritoResponseDto toDto(Carrito carrito) {
        if (carrito == null) {
            return null;
        }

        return CarritoResponseDto.builder()
                .id(carrito.getId())
                .usuarioId(carrito.getUsuarioId())
                .items(carrito.getItems() != null
                        ? carrito.getItems().stream()
                        .map(item -> ItemCarritoDto.builder()
                                .productoId(item.getProductoId())
                                .cantidad(item.getCantidad())
                                .build())
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
}

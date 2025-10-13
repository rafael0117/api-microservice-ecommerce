package com.rafael0117.carrito_service.application.mapper;

import com.rafael0117.carrito_service.domain.model.Carrito;
import com.rafael0117.carrito_service.web.dto.carrito.CarritoRequestDto;
import com.rafael0117.carrito_service.web.dto.carrito.CarritoResponseDto;

public interface CarritoMapper {
    Carrito toDomain(CarritoRequestDto dto);
    CarritoResponseDto toDto(Carrito carrito);
}

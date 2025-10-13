package com.rafael0117.carrito_service.application.service;

import com.rafael0117.carrito_service.web.dto.carrito.CarritoRequestDto;
import com.rafael0117.carrito_service.web.dto.carrito.CarritoResponseDto;

public interface CarritoService {
    CarritoResponseDto agregarAlCarrito(CarritoRequestDto request);
    CarritoResponseDto obtenerCarrito(Long usuarioId);
}

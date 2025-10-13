package com.rafael0117.carrito_service.web.controller;

import com.rafael0117.carrito_service.application.service.CarritoService;
import com.rafael0117.carrito_service.web.dto.carrito.CarritoRequestDto;
import com.rafael0117.carrito_service.web.dto.carrito.CarritoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @PostMapping
    public ResponseEntity<CarritoResponseDto> agregar(@RequestBody CarritoRequestDto request) {
        return ResponseEntity.ok(carritoService.agregarAlCarrito(request));
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoResponseDto> obtener(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(usuarioId));
    }
}
package com.rafael0117.carrito_service.web.controller;

import com.rafael0117.carrito_service.application.service.CarritoService;
import com.rafael0117.carrito_service.domain.model.Carrito;
import com.rafael0117.carrito_service.domain.model.DetalleCarrito;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {
    private final CarritoService carritoService;

    @PostMapping("/agregar")
    public ResponseEntity<Carrito> agregarProducto(@RequestBody DetalleCarrito detalle) {
        Long idUsuario = 1L; // usuario fijo de prueba
        return ResponseEntity.ok(carritoService.agregarProducto(idUsuario, detalle));
    }

    @GetMapping({ "", "/1" })
    public ResponseEntity<Carrito> obtenerCarrito() {
        Long idUsuario = 1L;
        return ResponseEntity.ok(carritoService.obtenerCarrito(idUsuario));
    }

    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long productoId) {
        Long idUsuario = 1L; // usuario de prueba
        carritoService.eliminarProducto(idUsuario, productoId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/vaciar")
    public ResponseEntity<Void> vaciarCarrito() {
        Long idUsuario = 1L;
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.noContent().build();
    }

}

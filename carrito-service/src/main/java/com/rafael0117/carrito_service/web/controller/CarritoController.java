package com.rafael0117.carrito_service.web.controller;

import com.rafael0117.carrito_service.application.client.UsuarioClient;
import com.rafael0117.carrito_service.application.client.UsuarioDTO;
import com.rafael0117.carrito_service.application.service.CarritoService;
import com.rafael0117.carrito_service.domain.model.Carrito;
import com.rafael0117.carrito_service.domain.model.DetalleCarrito;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioClient usuarioClient;

    // 🔐 Agregar producto (requiere sesión)
    @PostMapping("/agregar")
    public ResponseEntity<Carrito> agregarProducto(@RequestBody DetalleCarrito detalle,
                                                   @RequestHeader("Authorization") String token) {
        UsuarioDTO usuario = usuarioClient.me(token);
        Long idUsuario = usuario.getId();
        return ResponseEntity.ok(carritoService.agregarProducto(idUsuario, detalle));
    }

    // 🛒 Obtener carrito del usuario autenticado
    @GetMapping
    public ResponseEntity<Carrito> obtenerCarrito(@RequestHeader("Authorization") String token) {
        UsuarioDTO usuario = usuarioClient.me(token);
        return ResponseEntity.ok(carritoService.obtenerCarrito(usuario.getId()));
    }

    // ❌ Eliminar producto del carrito
    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long productoId,
                                                 @RequestHeader("Authorization") String token) {
        UsuarioDTO usuario = usuarioClient.me(token);
        carritoService.eliminarProducto(usuario.getId(), productoId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{idUsuario}/vaciar")
    public ResponseEntity<Void> vaciar(@PathVariable Long idUsuario) {
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{idUsuario}")
    public ResponseEntity<Carrito> obtenerCarritoPorId(@PathVariable Long idUsuario) {
        Carrito carrito = carritoService.obtenerCarrito(idUsuario);
        return ResponseEntity.ok(carrito);
    }
}
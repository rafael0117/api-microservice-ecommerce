package com.rafael0117.pedido_service.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "carrito-service", url = "${app.carrito.base-url}")
public interface CarritoClient {
    // Ajusta si tu carrito expone otra ruta (tú tenías fijo 1L; ideal es por usuario)
    @GetMapping("/api/carrito/{idUsuario}")
    CarritoDto obtenerCarrito(@PathVariable Long idUsuario);
    @GetMapping({ "", "/1" })
    @DeleteMapping("/api/carrito/{idUsuario}/vaciar")
    void vaciar(@PathVariable Long idUsuario);
}
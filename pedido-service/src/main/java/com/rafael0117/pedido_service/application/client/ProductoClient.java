package com.rafael0117.pedido_service.application.client;

import com.rafael0117.pedido_service.config.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(
        name = "producto-service",
        url = "${app.producto.base-url}",
        configuration = FeignSecurityConfig.class
)
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoResponseDto buscarPorId(@PathVariable Long id);
    @PostMapping("/api/productos/{id}/reservar")
    void reservar(@PathVariable Long id, @RequestParam Integer cantidad);

    @PostMapping("/api/productos/{id}/descontar")
    void descontar(@PathVariable Long id, @RequestParam Integer cantidad);

    @PostMapping("/api/productos/{id}/liberar")
    void liberar(@PathVariable Long id, @RequestParam Integer cantidad);
}

package com.rafael0117.pedido_service.application.client;

import com.rafael0117.pedido_service.config.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "carrito-service", url = "${app.carrito.base-url}"
        ,configuration = FeignSecurityConfig .class)
public interface CarritoClient {
    @GetMapping("/api/carrito/{idUsuario}")
    CarritoDto obtenerCarritoPorId(@PathVariable Long idUsuario);

    @DeleteMapping("/api/carrito/{idUsuario}/vaciar")
    void vaciar(@PathVariable Long idUsuario);
}
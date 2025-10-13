package com.rafael0117.pedido_service.application.client;

import com.rafael0117.pedido_service.config.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(
        name = "producto-service",
        url = "${app.producto.base-url}",
        configuration = FeignSecurityConfig.class
)
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoResponseDto buscarPorId(@PathVariable Long id);
}

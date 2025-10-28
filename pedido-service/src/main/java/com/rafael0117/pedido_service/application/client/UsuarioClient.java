package com.rafael0117.pedido_service.application.client;


import com.rafael0117.pedido_service.config.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "auth-service",
        url = "${app.auth.base-url}",
        configuration = FeignSecurityConfig.class
)
public interface UsuarioClient {

    @GetMapping("/api/auth/me")
    UsuarioDTO me(@RequestHeader("Authorization") String token);

}
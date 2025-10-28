package com.rafael0117.pedido_service.web.controller;

import com.rafael0117.pedido_service.application.client.UsuarioClient;
import com.rafael0117.pedido_service.application.client.UsuarioDTO;
import com.rafael0117.pedido_service.application.service.PedidoService;
import com.rafael0117.pedido_service.domain.model.EstadoPedido;
import com.rafael0117.pedido_service.domain.model.MetodoPago;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioClient usuarioClient;

    @PostMapping
    public ResponseEntity<PedidoResponseDto> crearPedido(
            @RequestBody PedidoRequestDto request,
            @RequestHeader("Authorization") String token) {

        // 🔹 Obtener usuario autenticado
        UsuarioDTO usuario = usuarioClient.me(token);
        Long idUsuario = usuario.getId();

        // 🔹 Crear el pedido usando el ID real del usuario
        PedidoResponseDto response = pedidoService.crearDesdeCarrito(idUsuario, request);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/estado-pedido")
    public List<EstadoPedido> getEstadosPedido() {
        return Arrays.asList(EstadoPedido.values());
    }

    @GetMapping("/metodo-pago")
    public List<MetodoPago> getMetodosPago() {
        return Arrays.asList(MetodoPago.values());
    }
}
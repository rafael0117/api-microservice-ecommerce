package com.rafael0117.pedido_service.web.controller;

import com.rafael0117.pedido_service.application.client.UsuarioClient;
import com.rafael0117.pedido_service.application.client.UsuarioDTO;
import com.rafael0117.pedido_service.application.service.PedidoService;
import com.rafael0117.pedido_service.domain.model.EstadoPedido;
import com.rafael0117.pedido_service.domain.model.MetodoPago;
import com.rafael0117.pedido_service.web.dto.PedidoAdminUpdateRequest;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;
    private final UsuarioClient usuarioClient;
    @GetMapping
    public ResponseEntity<List<PedidoResponseDto>> listarPedidos(
            @RequestHeader("Authorization") String token) {
        try {
            UsuarioDTO caller = usuarioClient.me(token);

            boolean esAdmin = caller.getRoles() != null && caller.getRoles().stream()
                    .anyMatch(r -> r.getName().equalsIgnoreCase("ROLE_ADMIN"));

            if (!esAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<PedidoResponseDto> pedidos = pedidoService.listarTodos();
            return ResponseEntity.ok(pedidos);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDto> crearPedido(
            @RequestBody PedidoRequestDto request,
            @RequestHeader("Authorization") String token) {
        UsuarioDTO usuario = usuarioClient.me(token);
        Long idUsuario = usuario.getId();
        PedidoResponseDto response = pedidoService.crearDesdeCarrito(idUsuario, request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/admin")
    public ResponseEntity<PedidoResponseDto> actualizarPedidoComoAdmin(
            @PathVariable Long id,
            @RequestBody PedidoAdminUpdateRequest req,
            @RequestHeader("Authorization") String token) {

        try {
            UsuarioDTO caller = usuarioClient.me(token);

            boolean esAdmin = caller.getRoles() != null && caller.getRoles().stream()
                    .anyMatch(r -> r.getName().equalsIgnoreCase("ROLE_ADMIN"));

            if (!esAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
            }

            PedidoResponseDto resp = pedidoService.actualizarComoAdmin(id, req);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
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
package com.rafael0117.pedido_service.web.dto;

import com.rafael0117.pedido_service.domain.model.EstadoPedido;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoAdminUpdateRequest {
    private EstadoPedido estado;     // NUEVO estado (opcional)
    private BigDecimal envio;        // opcional
    private BigDecimal descuento;    // opcional
    private String mensaje;          // mensaje para el cliente (opcional)
}
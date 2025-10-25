package com.rafael0117.pedido_service.web.dto.pedido;

import com.rafael0117.pedido_service.domain.model.EstadoPedido;
import com.rafael0117.pedido_service.domain.model.MetodoPago;
import com.rafael0117.pedido_service.web.dto.detallePedido.PedidoDetalleResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoResponseDto {
    private Long id;
    private Long idUsuario;
    private LocalDateTime fechaCreacion;
    private EstadoPedido estado;

    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal envio;
    private BigDecimal descuento;
    private BigDecimal total;

    private String direccionEnvio;
    private MetodoPago metodoPago;

    private List<PedidoDetalleResponseDto> detalles;
}

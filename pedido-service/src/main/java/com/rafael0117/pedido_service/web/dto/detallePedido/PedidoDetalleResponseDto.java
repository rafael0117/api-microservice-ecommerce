package com.rafael0117.pedido_service.web.dto.detallePedido;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoDetalleResponseDto {
    private Long id;
    private Long idProducto;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private String talla;
    private String color;
    private BigDecimal totalLinea;
}

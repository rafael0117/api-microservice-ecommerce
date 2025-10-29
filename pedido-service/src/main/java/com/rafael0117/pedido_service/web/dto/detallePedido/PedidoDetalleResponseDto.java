package com.rafael0117.pedido_service.web.dto.detallePedido;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

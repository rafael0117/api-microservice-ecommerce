package com.rafael0117.pedido_service.web.dto.detallePedido;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoDetalleResponseDto {

    private Long id;
    private Long idProducto;
    private String nombreProducto;
    private String descripcion;
    private BigDecimal precioUnitario;
    private List<String> talla;
    private List<String> color;
    private Integer cantidad;
    private List<String> imagenesBase64;
    private BigDecimal totalLinea;
}

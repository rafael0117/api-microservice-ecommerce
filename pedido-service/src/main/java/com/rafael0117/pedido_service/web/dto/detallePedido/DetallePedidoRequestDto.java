package com.rafael0117.pedido_service.web.dto.detallePedido;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetallePedidoRequestDto {
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;
}

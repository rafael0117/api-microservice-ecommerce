package com.rafael0117.pedido_service.web.dto.pedido;

import com.rafael0117.pedido_service.web.dto.detallePedido.DetallePedidoRequestDto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoRequestDto {
    private Long usuarioId;
    private List<DetallePedidoRequestDto> detalles;
}

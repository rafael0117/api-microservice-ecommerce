package com.rafael0117.pedido_service.web.dto.pedido;

import com.rafael0117.pedido_service.web.dto.detallePedido.DetallePedidoResponseDto;

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
    private Double total;
    private String estado;
    private LocalDateTime fechaCreacion;
    private List<DetallePedidoResponseDto> detalles;
}

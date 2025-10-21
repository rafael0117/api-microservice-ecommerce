package com.rafael0117.pedido_service.web.dto.pedido;

import com.rafael0117.pedido_service.domain.model.MetodoPago;
import com.rafael0117.pedido_service.web.dto.detallePedido.DetallePedidoRequestDto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoRequestDto {
    private Long idUsuario;          // si prefieres, tómalo del JWT/Header
    private String direccionEnvio;
    private MetodoPago metodoPago;

}

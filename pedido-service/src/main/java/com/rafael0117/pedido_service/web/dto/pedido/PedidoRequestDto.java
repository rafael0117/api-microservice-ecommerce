package com.rafael0117.pedido_service.web.dto.pedido;

import com.rafael0117.pedido_service.domain.model.MetodoPago;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoRequestDto {
    private String direccionEnvio;
    private MetodoPago metodoPago;

}

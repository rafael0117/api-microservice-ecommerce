package com.rafael0117.pedido_service.application.client;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoDto {
    private Long id;
    private Long idUsuario;
    private LocalDateTime fechaCreacion;
    private List<DetalleCarritoDto> detalles;
}
package com.rafael0117.pedido_service.application.client;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleCarritoDto {
    private Long idProducto;
    private String nombreProducto;
    private String descripcion;
    private BigDecimal precio;
    private Integer cantidad;
    private String talla;
    private String color;
}
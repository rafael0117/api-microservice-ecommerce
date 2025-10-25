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
    private Long id;
    private Long idProducto;
    private String nombreProducto; // puede venir vacío y lo recalculamos desde producto
    private BigDecimal precio;     // idem
    private Integer cantidad;
    private List<String> talla;
    private List<String> color;
}
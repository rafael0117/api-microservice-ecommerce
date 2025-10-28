package com.rafael0117.pedido_service.application.client;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private List<String> talla;
    private List<String> color;
    private Long categoriaId;
    private String categoriaNombre;  // para UI
    private List<String> imagenesBase64;
}

package com.rafael0117.carrito_service.application.client;

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
    private List<String> talla;   // lista de tallas disponibles
    private List<String> color;   // lista de colores disponibles
    private Long categoriaId;
    private String categoriaNombre;
    private List<String> imagenesBase64;
}

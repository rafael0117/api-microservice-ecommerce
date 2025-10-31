package com.rafael0117.carrito_service.kafka.events;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductChangedPayload {

    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;

    private List<String> tallas;
    private List<String> colores;

    private Long categoriaId;

    /**
     * CREATED | UPDATED | DELETED
     */
    private String changeType;
    private Integer reservado;
}

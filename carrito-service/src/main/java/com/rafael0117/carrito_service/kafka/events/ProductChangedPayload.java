package com.rafael0117.carrito_service.kafka.events;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductChangedPayload {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private List<String> tallas;
    private List<String> colores;
    private Long categoriaId;
    private String changeType; // "CREATED", "UPDATED", "DELETED"
}


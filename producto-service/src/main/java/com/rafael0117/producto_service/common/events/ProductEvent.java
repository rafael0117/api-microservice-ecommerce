package com.rafael0117.producto_service.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEvent {

    public enum Type {
        CREATED, UPDATED,
        STOCK_RESERVED, STOCK_DEDUCTED, STOCK_RELEASED
    }

    private Type type;
    private Long productId;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private Integer reservado;
    private Instant timestamp;


}

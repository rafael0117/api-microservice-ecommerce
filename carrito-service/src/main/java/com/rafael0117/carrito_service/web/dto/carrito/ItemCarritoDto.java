package com.rafael0117.carrito_service.web.dto.carrito;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCarritoDto {
    private Long productoId;
    private Integer cantidad;
}

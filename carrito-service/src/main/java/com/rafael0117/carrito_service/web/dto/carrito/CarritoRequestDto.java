package com.rafael0117.carrito_service.web.dto.carrito;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoRequestDto {
    private Long usuarioId;
    private List<ItemCarritoDto> items;
}

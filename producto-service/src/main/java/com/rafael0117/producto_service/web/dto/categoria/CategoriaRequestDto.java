package com.rafael0117.producto_service.web.dto.categoria;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaRequestDto {
    private String nombre;
    private Boolean estado;
}

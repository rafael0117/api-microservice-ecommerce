package com.rafael0117.producto_service.web.dto.categoria;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponseDto {
    private Long id;
    private String nombre;
}

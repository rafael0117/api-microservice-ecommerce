package com.rafael0117.producto_service.web.dto.producto;

import com.rafael0117.producto_service.domain.model.Categoria;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDto {
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private String talla;
    private String color;
    private Long categoriaId;

    // NUEVO: lista de imágenes en base64
    private List<String> imagenesBase64;
}
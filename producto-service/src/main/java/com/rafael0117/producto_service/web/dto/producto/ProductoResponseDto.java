package com.rafael0117.producto_service.web.dto.producto;

import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.domain.model.CategoriaSexo;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private CategoriaSexo categoriaSexo;
    private List<String> talla;
    private List<String> color;
    private Long categoriaId;
    private String categoriaNombre;  // para UI
    private List<String> imagenesBase64; // urls (o base64 si prefieres)
}


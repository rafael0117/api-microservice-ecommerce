package com.rafael0117.producto_service.web.dto.producto;

import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.domain.model.CategoriaSexo;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDto {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Integer reservado;
    private CategoriaSexo categoriaSexo;
    private List<String> talla;            // listas
    private List<String> color;            // listas
    private Long categoriaId;
    private List<String> imagenesBase64;   // opcional (si envías base64)
}
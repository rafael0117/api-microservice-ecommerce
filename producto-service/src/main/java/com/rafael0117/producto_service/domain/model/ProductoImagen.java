package com.rafael0117.producto_service.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto_imagen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoImagen {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String nombreArchivo;           // uuid.ext
    private String url;                     // /uploads/uuid.ext o /media/uuid.ext
    private Integer orden;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="producto_id")
    private Producto producto;
}

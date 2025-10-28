package com.rafael0117.carrito_service.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "detalle_carrito")
public class DetalleCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idProducto;
    private String nombreProducto;
    private String descripcion;

    @Column(precision = 18, scale = 2)
    private BigDecimal precio;

    private Integer cantidad;
    private String talla;            // <-- selección
    private String color;            // <-- selección



    private Long categoriaId;
    private String categoriaNombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito")
    @JsonBackReference("carrito-detalle")
    private Carrito carrito;
}

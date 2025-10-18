package com.rafael0117.carrito_service.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "detalle_carrito")
public class DetalleCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idProducto;
    private String nombreProducto;
    private Double precio;
    private Integer cantidad;
    private String talla;
    private String color;

    @ManyToOne
    @JoinColumn(name = "id_carrito")
    @JsonBackReference(value = "carrito-detalle")
    private Carrito carrito;
}

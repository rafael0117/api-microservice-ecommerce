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

    @ElementCollection
    @CollectionTable(name = "detalle_tallas", joinColumns = @JoinColumn(name = "detalle_id"))
    @Column(name = "talla")
    private List<String> tallas = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "detalle_colores", joinColumns = @JoinColumn(name = "detalle_id"))
    @Column(name = "color")
    private List<String> colores = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "detalle_imagenes", joinColumns = @JoinColumn(name = "detalle_id"))
    @Column(name = "imagen_base64", columnDefinition = "LONGTEXT")
    private List<String> imagenesBase64 = new ArrayList<>();

    private Long categoriaId;
    private String categoriaNombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito")
    @JsonBackReference("carrito-detalle")
    private Carrito carrito;
}

package com.rafael0117.producto_service.domain.model;

import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "producto")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Integer reservado;

    @ElementCollection
    @CollectionTable(name = "producto_talla", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "valor")
    private List<String> tallas = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "producto_color", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "valor")
    private List<String> colores = new ArrayList<>();

    // ===== Categoría (FK directa + relación de solo lectura) =====
    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", insertable = false, updatable = false)
    private Categoria categoria;


    @Builder.Default
    @OneToMany(mappedBy="producto", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    private List<ProductoImagen> imagenesBase64 = new ArrayList<>();


}
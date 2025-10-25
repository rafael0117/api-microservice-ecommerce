package com.rafael0117.pedido_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "pedido_detalles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idProducto;
    private String nombreProducto;
    private String descripcion;

    @Column(precision = 18, scale = 2)
    private BigDecimal precioUnitario;

    private Integer cantidad;

    // ✅ Listas para tallas y colores seleccionados
    @ElementCollection
    @CollectionTable(name = "pedido_detalle_tallas", joinColumns = @JoinColumn(name = "detalle_id"))
    @Column(name = "talla")
    private List<String> tallas;

    @ElementCollection
    @CollectionTable(name = "pedido_detalle_colores", joinColumns = @JoinColumn(name = "detalle_id"))
    @Column(name = "color")
    private List<String> colores;

    @Column(precision = 18, scale = 2)
    private BigDecimal totalLinea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
}

package com.rafael0117.pedido_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity @Table(name = "pedido_detalles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoDetalle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idProducto;
    private String nombreProducto;

    @Column(precision = 18, scale = 2) private BigDecimal precioUnitario;
    private Integer cantidad;
    private List<String> talla;
    private List<String> color;

    @Column(precision = 18, scale = 2) private BigDecimal totalLinea;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pedido_id")
    private Pedido pedido;
}
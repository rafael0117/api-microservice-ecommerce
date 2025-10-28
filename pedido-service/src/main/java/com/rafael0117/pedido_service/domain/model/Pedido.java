package com.rafael0117.pedido_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pedido {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado; // CREATED | CONFIRMED | CANCELLED | FAILED, etc.

    @Column(precision = 18, scale = 2) private BigDecimal subtotal;
    @Column(precision = 18, scale = 2) private BigDecimal impuesto;
    @Column(precision = 18, scale = 2) private BigDecimal envio;      // ← nuevo
    @Column(precision = 18, scale = 2) private BigDecimal descuento;  // ← nuevo
    @Column(precision = 18, scale = 2) private BigDecimal total;

    private String direccionEnvio;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoDetalle> detalles = new ArrayList<>();

    // Pedido.java (solo el @PrePersist y defaults relevantes)
    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (estado == null) estado = EstadoPedido.PENDING;
        if (envio == null) envio = new BigDecimal("0.0");      // 1 decimal
        if (descuento == null) descuento = new BigDecimal("0.0"); // 1 decimal
    }

}

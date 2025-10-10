package com.rafael0117.pedido_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_pedido")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productoId; // viene del producto-service
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
}
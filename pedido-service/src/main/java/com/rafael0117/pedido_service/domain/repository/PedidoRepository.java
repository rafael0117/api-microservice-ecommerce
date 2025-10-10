package com.rafael0117.pedido_service.domain.repository;

import com.rafael0117.pedido_service.domain.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido,Long> {
}

package com.rafael0117.pedido_service.application.mapper;

import com.rafael0117.pedido_service.domain.model.Pedido;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoResponseDto;

public interface PedidoMapper {
    Pedido toDomain(PedidoRequestDto dto);
    PedidoResponseDto toDto(Pedido pedido);
}

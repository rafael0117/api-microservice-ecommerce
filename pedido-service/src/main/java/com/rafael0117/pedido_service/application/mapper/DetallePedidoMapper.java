package com.rafael0117.pedido_service.application.mapper;

import com.rafael0117.pedido_service.application.client.ProductoResponseDto;
import com.rafael0117.pedido_service.domain.model.DetallePedido;
import com.rafael0117.pedido_service.web.dto.detallePedido.DetallePedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.detallePedido.DetallePedidoResponseDto;

public interface DetallePedidoMapper {
    DetallePedido toDomain(DetallePedidoRequestDto detalleDto, ProductoResponseDto producto);
    DetallePedidoResponseDto toDto(DetallePedido detalle);
}

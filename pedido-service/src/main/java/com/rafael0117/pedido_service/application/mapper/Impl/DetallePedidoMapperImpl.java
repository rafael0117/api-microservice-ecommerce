package com.rafael0117.pedido_service.application.mapper.Impl;

import com.rafael0117.pedido_service.application.client.ProductoResponseDto;
import com.rafael0117.pedido_service.application.mapper.DetallePedidoMapper;
import com.rafael0117.pedido_service.domain.model.DetallePedido;
import com.rafael0117.pedido_service.web.dto.detallePedido.DetallePedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.detallePedido.DetallePedidoResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DetallePedidoMapperImpl implements DetallePedidoMapper {
    @Override
    public DetallePedido toDomain(DetallePedidoRequestDto detalleDto, ProductoResponseDto producto) {
        Double subtotal = producto.getPrecio() * detalleDto.getCantidad();

        return DetallePedido.builder()
                .productoId(producto.getId())
                .precioUnitario(producto.getPrecio())
                .cantidad(detalleDto.getCantidad())
                .subtotal(subtotal)
                .build();
    }
    @Override
    public DetallePedidoResponseDto toDto(DetallePedido detalle) {
        return DetallePedidoResponseDto.builder()
                .id(detalle.getId())
                .productoId(detalle.getProductoId())
                .precioUnitario(detalle.getPrecioUnitario())
                .cantidad(detalle.getCantidad())
                .subtotal(detalle.getSubtotal())
                .build();
    }
}

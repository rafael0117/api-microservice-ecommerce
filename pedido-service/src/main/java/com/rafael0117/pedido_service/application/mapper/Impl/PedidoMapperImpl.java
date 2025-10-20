package com.rafael0117.pedido_service.application.mapper.Impl;

import com.rafael0117.pedido_service.application.mapper.PedidoMapper;
import com.rafael0117.pedido_service.domain.model.Pedido;
import com.rafael0117.pedido_service.domain.model.PedidoDetalle;
import com.rafael0117.pedido_service.web.dto.detallePedido.PedidoDetalleResponseDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class PedidoMapperImpl implements PedidoMapper {
    @Override
    public PedidoResponseDto toDto(Pedido p) {
        if (p == null) return null;
        return PedidoResponseDto.builder()
                .id(p.getId())
                .idUsuario(p.getIdUsuario())
                .fechaCreacion(p.getFechaCreacion())
                .estado(p.getEstado())
                .subtotal(p.getSubtotal())
                .impuesto(p.getImpuesto())
                .total(p.getTotal())
                .direccionEnvio(p.getDireccionEnvio())
                .metodoPago(p.getMetodoPago())
                .detalles(p.getDetalles().stream().map(this::toDetalleDto).toList())
                .build();
    }

    @Override
    public PedidoDetalleResponseDto toDetalleDto(PedidoDetalle d) {
        if (d == null) return null;
        return PedidoDetalleResponseDto.builder()
                .id(d.getId())
                .idProducto(d.getIdProducto())
                .nombreProducto(d.getNombreProducto())
                .precioUnitario(d.getPrecioUnitario())
                .cantidad(d.getCantidad())
                .talla(d.getTalla())
                .color(d.getColor())
                .totalLinea(d.getTotalLinea())
                .build();
    }
}

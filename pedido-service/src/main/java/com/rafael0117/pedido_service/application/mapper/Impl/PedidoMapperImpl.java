package com.rafael0117.pedido_service.application.mapper.Impl;

import com.rafael0117.pedido_service.application.mapper.PedidoMapper;
import com.rafael0117.pedido_service.domain.model.Pedido;
import com.rafael0117.pedido_service.domain.model.PedidoDetalle;
import com.rafael0117.pedido_service.web.dto.detallePedido.PedidoDetalleResponseDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;


@Component
public class PedidoMapperImpl implements PedidoMapper {

    private static BigDecimal d1(BigDecimal v) {
        if (v == null) return null;
        // fuerza 1 decimal exactamente, p.e. 0.0, 180.0, 32.4
        return v.setScale(1, RoundingMode.HALF_UP);
    }

    @Override
    public PedidoResponseDto toDto(Pedido p) {
        if (p == null) return null;

        return PedidoResponseDto.builder()
                .id(p.getId())
                .idUsuario(p.getIdUsuario())
                .fechaCreacion(p.getFechaCreacion() != null ? p.getFechaCreacion().truncatedTo(ChronoUnit.SECONDS) : null)
                .estado(p.getEstado() != null ? p.getEstado().name() : "CREATED")
                .subtotal(d1(p.getSubtotal()))
                .impuesto(d1(p.getImpuesto()))
                .envio(d1(p.getEnvio()))
                .descuento(d1(p.getDescuento()))
                .total(d1(p.getTotal()))
                .direccionEnvio(p.getDireccionEnvio())
                .metodoPago(p.getMetodoPago() != null ? p.getMetodoPago().name() : null)
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
                .precioUnitario(d1(d.getPrecioUnitario()))
                .cantidad(d.getCantidad())
                .talla(d.getTalla())     // ✅ aquí
                .color(d.getColor())   // ✅ aquí
                .totalLinea(d1(d.getTotalLinea()))
                .build();
    }


}
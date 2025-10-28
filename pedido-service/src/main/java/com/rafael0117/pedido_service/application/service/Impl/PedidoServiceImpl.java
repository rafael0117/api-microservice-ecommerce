package com.rafael0117.pedido_service.application.service.Impl;

import com.rafael0117.pedido_service.application.client.*;
import com.rafael0117.pedido_service.application.mapper.PedidoMapper;
import com.rafael0117.pedido_service.application.service.PedidoService;
import com.rafael0117.pedido_service.domain.model.EstadoPedido;
import com.rafael0117.pedido_service.domain.model.PedidoDetalle;
import com.rafael0117.pedido_service.domain.model.Pedido;
import com.rafael0117.pedido_service.domain.repository.PedidoRepository;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final CarritoClient carritoClient;

    private static final BigDecimal IGV = new BigDecimal("0.18");

    @Override
    @Transactional
    public PedidoResponseDto crearDesdeCarrito(Long idUsuario, PedidoRequestDto request) {
        if (idUsuario == null) throw new IllegalArgumentException("idUsuario es requerido");

        // 1) Obtener carrito
        CarritoDto carrito = carritoClient.obtenerCarritoPorId(idUsuario);
        if (carrito == null || carrito.getDetalles() == null || carrito.getDetalles().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío.");
        }

        // 2) Construir pedido
        Pedido pedido = Pedido.builder()
                .idUsuario(idUsuario)
                .estado(EstadoPedido.PENDING)
                .direccionEnvio(request.getDireccionEnvio())
                .metodoPago(request.getMetodoPago())
                .subtotal(BigDecimal.ZERO.setScale(2))
                .impuesto(BigDecimal.ZERO.setScale(2))
                .envio(BigDecimal.ZERO.setScale(2))
                .descuento(BigDecimal.ZERO.setScale(2))
                .total(BigDecimal.ZERO.setScale(2))
                .build();

        BigDecimal subtotal = BigDecimal.ZERO.setScale(2);

        for (DetalleCarritoDto d : carrito.getDetalles()) {
            BigDecimal precioUnitario = (d.getPrecio() != null ? d.getPrecio() : BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalLinea = precioUnitario
                    .multiply(BigDecimal.valueOf(d.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            PedidoDetalle det = PedidoDetalle.builder()
                    .pedido(pedido)
                    .idProducto(d.getIdProducto())
                    .nombreProducto(d.getNombreProducto())
                    .descripcion(d.getDescripcion())
                    .precioUnitario(precioUnitario)
                    .cantidad(d.getCantidad())
                    .tallas(d.getTallas())
                    .colores(d.getColores())
                    .totalLinea(totalLinea)
                    .build();

            pedido.getDetalles().add(det);
            subtotal = subtotal.add(totalLinea).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal impuesto = subtotal.multiply(IGV).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(impuesto).setScale(2, RoundingMode.HALF_UP);

        pedido.setSubtotal(subtotal);
        pedido.setImpuesto(impuesto);
        pedido.setTotal(total);

        pedido = pedidoRepository.save(pedido);

        try {
            carritoClient.vaciar(idUsuario);
        } catch (Exception e) {
            // log.warn("No se pudo vaciar el carrito del usuario {}: {}", idUsuario, e.getMessage());
        }

        return pedidoMapper.toDto(pedido);
    }

}
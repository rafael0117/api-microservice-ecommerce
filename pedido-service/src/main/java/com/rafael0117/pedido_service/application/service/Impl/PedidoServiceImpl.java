package com.rafael0117.pedido_service.application.service.Impl;

import com.rafael0117.pedido_service.application.client.*;
import com.rafael0117.pedido_service.application.mapper.PedidoMapper;
import com.rafael0117.pedido_service.application.service.PedidoService;
import com.rafael0117.pedido_service.domain.model.EstadoPedido;
import com.rafael0117.pedido_service.domain.model.PedidoDetalle;
import com.rafael0117.pedido_service.domain.model.Pedido;
import com.rafael0117.pedido_service.domain.repository.PedidoRepository;
import com.rafael0117.pedido_service.web.dto.detallePedido.DetallePedidoRequestDto;
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
    private final ProductoClient productoClient;

    private static final BigDecimal IGV = new BigDecimal("0.18");

    @Override
    @Transactional
    public PedidoResponseDto crearDesdeCarrito(PedidoRequestDto request) {
        Long idUsuario = request.getIdUsuario();
        if (idUsuario == null) throw new IllegalArgumentException("idUsuario es requerido");

        CarritoDto carrito = carritoClient.obtenerCarrito(idUsuario);
        if (carrito == null || carrito.getDetalles() == null || carrito.getDetalles().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío.");
        }

        Pedido pedido = Pedido.builder()
                .idUsuario(idUsuario)
                .estado(EstadoPedido.PENDING)
                .direccionEnvio(request.getDireccionEnvio())
                .metodoPago(request.getMetodoPago())
                .subtotal(BigDecimal.ZERO)
                .impuesto(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;

        // 1) Revalidar cada línea con producto-service + reservar stock
        for (DetalleCarritoDto d : carrito.getDetalles()) {
            ProductoResponseDto p = productoClient.buscarPorId(d.getIdProducto());
            if (p == null) {
                throw new IllegalStateException("Producto no encontrado: " + d.getIdProducto());
            }
            if (p.getStock() == null || p.getStock() < d.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para: " + p.getNombre());
            }

            // Reserva
            productoClient.reservar(p.getId(), d.getCantidad());

            BigDecimal precioUnitario = p.getPrecio() != null ? p.getPrecio()
                    : (d.getPrecio() != null ? d.getPrecio() : BigDecimal.ZERO);
            BigDecimal totalLinea = precioUnitario
                    .multiply(BigDecimal.valueOf(d.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            PedidoDetalle det = PedidoDetalle.builder()
                    .pedido(pedido)
                    .idProducto(p.getId())
                    .nombreProducto(p.getNombre())
                    .precioUnitario(precioUnitario.setScale(2, RoundingMode.HALF_UP))
                    .cantidad(d.getCantidad())
                    .talla(d.getTalla())
                    .color(d.getColor())
                    .totalLinea(totalLinea)
                    .build();

            pedido.getDetalles().add(det);
            subtotal = subtotal.add(totalLinea);
        }

        // 2) Totales
        BigDecimal impuesto = subtotal.multiply(IGV).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(impuesto)
                .max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        pedido.setSubtotal(subtotal);
        pedido.setImpuesto(impuesto);
        pedido.setTotal(total);

        // 3) Guardar PENDING
        pedido = pedidoRepository.save(pedido);

        try {
            // 4) Confirmar: descontar stock definitivo
            for (PedidoDetalle det : pedido.getDetalles()) {
                productoClient.descontar(det.getIdProducto(), det.getCantidad());
            }
            // 5) Vaciar carrito del usuario
            carritoClient.vaciar(idUsuario);

            // 6) Estado confirmado
            pedido.setEstado(EstadoPedido.CONFIRMED);
            pedido = pedidoRepository.save(pedido);

            return pedidoMapper.toDto(pedido);

        } catch (Exception e) {
            // Compensación: liberar reservas si algo falló
            for (PedidoDetalle det : pedido.getDetalles()) {
                try { productoClient.liberar(det.getIdProducto(), det.getCantidad()); }
                catch (Exception ignore) {}
            }
            pedido.setEstado(EstadoPedido.FAILED);
            pedidoRepository.save(pedido);
            throw new IllegalStateException("No se pudo confirmar el pedido: " + e.getMessage(), e);
        }
    }

    private BigDecimal nullSafe(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
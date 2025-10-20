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

    private static final BigDecimal IGV = new BigDecimal("0.18");

    @Override
    @Transactional
    public PedidoResponseDto crearDesdeCarrito(PedidoRequestDto request) {
        Long idUsuario = request.getIdUsuario();
        if (idUsuario == null)
            throw new IllegalArgumentException("idUsuario es requerido");

        // 🔹 Obtener el carrito completo del usuario
        CarritoDto carrito = carritoClient.obtenerCarritoPorId(idUsuario);
        if (carrito == null || carrito.getDetalles() == null || carrito.getDetalles().isEmpty())
            throw new IllegalStateException("El carrito está vacío.");

        // 🔹 Crear el pedido base
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

        // 🔹 Convertir los productos del carrito en detalles del pedido
        for (DetalleCarritoDto d : carrito.getDetalles()) {
            BigDecimal precioUnitario = d.getPrecio() != null ? d.getPrecio() : BigDecimal.ZERO;
            BigDecimal totalLinea = precioUnitario
                    .multiply(BigDecimal.valueOf(d.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            PedidoDetalle det = PedidoDetalle.builder()
                    .pedido(pedido)
                    .idProducto(d.getIdProducto())
                    .nombreProducto(d.getNombreProducto())
                    .precioUnitario(precioUnitario)
                    .cantidad(d.getCantidad())
                    .talla(d.getTalla())
                    .color(d.getColor())
                    .totalLinea(totalLinea)
                    .build();

            pedido.getDetalles().add(det);
            subtotal = subtotal.add(totalLinea);
        }

        // 🔹 Calcular montos finales
        BigDecimal impuesto = subtotal.multiply(IGV).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(impuesto).setScale(2, RoundingMode.HALF_UP);

        pedido.setSubtotal(subtotal);
        pedido.setImpuesto(impuesto);
        pedido.setTotal(total);

        // 🔹 Guardar pedido
        pedido = pedidoRepository.save(pedido);

        try {
            // 🧹 Vaciar el carrito del usuario con el nuevo endpoint
            carritoClient.vaciar(idUsuario);

            pedido.setEstado(EstadoPedido.CONFIRMED);
            pedido = pedidoRepository.save(pedido);
            return pedidoMapper.toDto(pedido);

        } catch (Exception e) {
            pedido.setEstado(EstadoPedido.FAILED);
            pedidoRepository.save(pedido);
            throw new IllegalStateException("No se pudo confirmar el pedido: " + e.getMessage(), e);
        }
    }
}
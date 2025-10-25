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
    public PedidoResponseDto crearDesdeCarrito(PedidoRequestDto request) {
        Long idUsuario = request.getIdUsuario();
        if (idUsuario == null) throw new IllegalArgumentException("idUsuario es requerido");

        // 1) Obtener carrito
        CarritoDto carrito = carritoClient.obtenerCarritoPorId(idUsuario);
        if (carrito == null || carrito.getDetalles() == null || carrito.getDetalles().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío.");
        }

        // 2) Construir pedido base
        Pedido pedido = Pedido.builder()
                .idUsuario(idUsuario)
                .estado(EstadoPedido.PENDING)              // ← requerido por el JSON de salida
                .direccionEnvio(request.getDireccionEnvio())
                .metodoPago(request.getMetodoPago())
                .subtotal(BigDecimal.ZERO.setScale(2))
                .impuesto(BigDecimal.ZERO.setScale(2))
                .envio(new BigDecimal("0.00"))             // ← 0.00
                .descuento(new BigDecimal("0.00"))         // ← 0.00
                .total(BigDecimal.ZERO.setScale(2))
                .build();

        BigDecimal subtotal = BigDecimal.ZERO.setScale(2);

        // 3) Detalles desde el carrito (copiando talla/color seleccionados)
        for (DetalleCarritoDto d : carrito.getDetalles()) {
            BigDecimal precioUnitario = (d.getPrecio() != null ? d.getPrecio() : BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
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

        // 4) Montos
        BigDecimal impuesto = subtotal.multiply(IGV).setScale(2, RoundingMode.HALF_UP);
        BigDecimal envio = pedido.getEnvio();         // 0.00
        BigDecimal descuento = pedido.getDescuento(); // 0.00
        BigDecimal total = subtotal.add(impuesto).add(envio).subtract(descuento).setScale(2, RoundingMode.HALF_UP);

        pedido.setSubtotal(subtotal);
        pedido.setImpuesto(impuesto);
        pedido.setTotal(total);

        // 5) Persistir
        pedido = pedidoRepository.save(pedido);

        // 6) Vaciar carrito (no cambiar estado aquí para mantener "CREATED" en la respuesta)
        try {
            carritoClient.vaciar(idUsuario);
        } catch (Exception e) {
            // Loguea si quieres, pero no cambies el estado para que el JSON siga en CREATED
            // log.warn("No se pudo vaciar el carrito del usuario {}: {}", idUsuario, e.getMessage());
        }

        // 7) Mapear a DTO (tu mapper ya se encarga de formateo adicional)
        return pedidoMapper.toDto(pedido);
    }
}
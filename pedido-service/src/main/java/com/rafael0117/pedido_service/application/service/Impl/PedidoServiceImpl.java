package com.rafael0117.pedido_service.application.service.Impl;

import com.rafael0117.pedido_service.application.client.*;
import com.rafael0117.pedido_service.application.mapper.PedidoMapper;
import com.rafael0117.pedido_service.application.service.EmailService;
import com.rafael0117.pedido_service.application.service.PedidoService;
import com.rafael0117.pedido_service.domain.model.EstadoPedido;
import com.rafael0117.pedido_service.domain.model.PedidoDetalle;
import com.rafael0117.pedido_service.domain.model.Pedido;
import com.rafael0117.pedido_service.domain.repository.PedidoRepository;
import com.rafael0117.pedido_service.web.dto.PedidoAdminUpdateRequest;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final CarritoClient carritoClient;
    private final UsuarioClient usuarioClient;
    private final EmailService emailService;


    private static final BigDecimal IGV = new BigDecimal("0.18");
    @Override
    public List<PedidoResponseDto> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedidoMapper::toDto)
                .toList();
    }

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
                .detalles(new ArrayList<>())
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
                    .talla(d.getTalla())      // directamente como String
                    .color(d.getColor())     // directamente como String
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

        // Guardar pedido
        pedido = pedidoRepository.save(pedido);

        // Vaciar carrito
        try {
            carritoClient.vaciar(idUsuario);
        } catch (Exception e) {
            // log.warn("No se pudo vaciar el carrito del usuario {}: {}", idUsuario, e.getMessage());
        }

        return pedidoMapper.toDto(pedido);
    }

    @Transactional
    @Override
    public PedidoResponseDto actualizarComoAdmin(Long idPedido, PedidoAdminUpdateRequest req) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        if (req.getEnvio() != null)      pedido.setEnvio(req.getEnvio());
        if (req.getDescuento() != null)  pedido.setDescuento(req.getDescuento());
        if (req.getEstado() != null)     pedido.setEstado(req.getEstado());

        // Recalcular total: subtotal + impuesto + envio - descuento
        BigDecimal total = (pedido.getSubtotal()  != null ? pedido.getSubtotal()  : BigDecimal.ZERO)
                .add(pedido.getImpuesto()        != null ? pedido.getImpuesto()  : BigDecimal.ZERO)
                .add(pedido.getEnvio()           != null ? pedido.getEnvio()     : BigDecimal.ZERO)
                .subtract(pedido.getDescuento()  != null ? pedido.getDescuento() : BigDecimal.ZERO);

        pedido.setTotal(total);

        pedido = pedidoRepository.save(pedido);

        // Obtener email del cliente del pedido
        UsuarioDTO cliente = usuarioClient.buscarPorId(pedido.getIdUsuario()); // agrega este método al Feign si no existe
        if (cliente != null && cliente.getEmail() != null) {
            emailService.enviarCambioEstado(
                    cliente.getEmail(),
                    cliente.getUsername(),
                    pedido.getId(),
                    pedido.getEstado(),
                    req.getMensaje()
            );
        }

        return pedidoMapper.toDto(pedido);
    }

}
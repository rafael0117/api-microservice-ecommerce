package com.rafael0117.pedido_service.application.service.Impl;

import com.rafael0117.pedido_service.application.client.ProductoClient;
import com.rafael0117.pedido_service.application.client.ProductoResponseDto;
import com.rafael0117.pedido_service.application.mapper.DetallePedidoMapper;
import com.rafael0117.pedido_service.application.mapper.PedidoMapper;
import com.rafael0117.pedido_service.application.service.PedidoService;
import com.rafael0117.pedido_service.domain.model.DetallePedido;
import com.rafael0117.pedido_service.domain.model.Pedido;
import com.rafael0117.pedido_service.domain.repository.PedidoRepository;
import com.rafael0117.pedido_service.web.dto.detallePedido.DetallePedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ProductoClient productoClient;
    private final PedidoMapper pedidoMapper;
    private final DetallePedidoMapper detallePedidoMapper;
    @Override
    @Transactional
    public PedidoResponseDto crearPedido(PedidoRequestDto request) {
        Pedido pedido = pedidoMapper.toDomain(request);
        double total = 0.0;
        for (DetallePedidoRequestDto detalleDto : request.getDetalles()) {
            ProductoResponseDto producto = productoClient.buscarPorId(detalleDto.getProductoId());
            DetallePedido detalle = detallePedidoMapper.toDomain(detalleDto, producto);
            detalle.setPedido(pedido);
            pedido.getDetalles().add(detalle);
            total += detalle.getSubtotal();
        }
        pedido.setTotal(total);
        Pedido saved = pedidoRepository.save(pedido);
        return pedidoMapper.toDto(saved);
    }
}

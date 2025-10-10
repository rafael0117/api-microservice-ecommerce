package com.rafael0117.pedido_service.application.mapper.Impl;

import com.rafael0117.pedido_service.application.mapper.DetallePedidoMapper;
import com.rafael0117.pedido_service.application.mapper.PedidoMapper;
import com.rafael0117.pedido_service.domain.model.Estado;
import com.rafael0117.pedido_service.domain.model.Pedido;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class PedidoMapperImpl implements PedidoMapper {
    private final DetallePedidoMapper detallePedidoMapper;
    @Override
    public Pedido toDomain(PedidoRequestDto dto) {
        return Pedido.builder()
                .usuarioId(1L)
                .fechaCreacion(LocalDateTime.now())
                .estado(Estado.PENDIENTE)
                .build();
    }

    @Override
    public PedidoResponseDto toDto(Pedido pedido) {
        return PedidoResponseDto.builder()
                .id(pedido.getId())
                .fechaCreacion(pedido.getFechaCreacion())
                .total(pedido.getTotal())
                .estado(String.valueOf(pedido.getEstado()))
                .detalles(
                        pedido.getDetalles().stream()
                                .map(detallePedidoMapper::toDto)
                                .toList()
                )
                .build();
    }
}

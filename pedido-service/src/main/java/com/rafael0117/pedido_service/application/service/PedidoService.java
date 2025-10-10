package com.rafael0117.pedido_service.application.service;

import com.rafael0117.pedido_service.web.dto.pedido.PedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoResponseDto;

public interface PedidoService {
    PedidoResponseDto crearPedido(PedidoRequestDto request);
}

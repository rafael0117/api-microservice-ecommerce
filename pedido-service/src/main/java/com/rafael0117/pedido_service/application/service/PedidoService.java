package com.rafael0117.pedido_service.application.service;

import com.rafael0117.pedido_service.web.dto.PedidoAdminUpdateRequest;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoRequestDto;
import com.rafael0117.pedido_service.web.dto.pedido.PedidoResponseDto;

import java.util.List;

public interface PedidoService {
    PedidoResponseDto crearDesdeCarrito(Long idUsuario, PedidoRequestDto request);
    PedidoResponseDto actualizarComoAdmin(Long idPedido, PedidoAdminUpdateRequest req);
    List<PedidoResponseDto> listarTodos();

}

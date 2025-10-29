package com.rafael0117.pedido_service.application.service;

import com.rafael0117.pedido_service.domain.model.EstadoPedido;

public interface EmailService {
    void enviarCambioEstado(String to, String nombre, Long idPedido, EstadoPedido estado, String mensaje);
}

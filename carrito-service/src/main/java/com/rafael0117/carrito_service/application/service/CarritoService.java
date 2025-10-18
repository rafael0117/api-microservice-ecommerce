package com.rafael0117.carrito_service.application.service;

import com.rafael0117.carrito_service.domain.model.Carrito;
import com.rafael0117.carrito_service.domain.model.DetalleCarrito;


public interface CarritoService {
    Carrito obtenerCarrito(Long idUsuario);
    Carrito agregarProducto(Long idUsuario, DetalleCarrito detalle);
    void eliminarProducto(Long idUsuario, Long productoId);
    void vaciarCarrito(Long idUsuario);
}

package com.rafael0117.producto_service.exception;

public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(Long id) {
        super("Producto no encontrado: " + id);
    }
}
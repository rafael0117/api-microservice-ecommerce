package com.rafael0117.producto_service.exception;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String msg) { super(msg); }
}
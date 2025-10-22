package com.rafael0117.producto_service.application.service;

import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductoService {
    List<ProductoResponseDto> listar();
    ProductoResponseDto guardar(ProductoRequestDto productoRequestDto);
    public ProductoResponseDto actualizar(Long id, ProductoRequestDto dto);
    ProductoResponseDto buscarPorId(Long id);
    void eliminar(Long id);
    void reservar(Long id, int cantidad);
    void descontar(Long id, int cantidad);
    void liberar(Long id, int cantidad);
}

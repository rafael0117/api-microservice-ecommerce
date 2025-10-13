package com.rafael0117.carrito_service.application.service.Impl;

import com.rafael0117.carrito_service.application.client.ProductoClient;
import com.rafael0117.carrito_service.application.mapper.CarritoMapper;
import com.rafael0117.carrito_service.application.service.CarritoService;
import com.rafael0117.carrito_service.domain.model.Carrito;
import com.rafael0117.carrito_service.domain.repository.CarritoRepository;
import com.rafael0117.carrito_service.web.dto.carrito.CarritoRequestDto;
import com.rafael0117.carrito_service.web.dto.carrito.CarritoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarritoServiceImpl implements CarritoService {
    private final CarritoRepository carritoRepository;
    private final ProductoClient productoClient;
    private final CarritoMapper carritoMapper;
    @Override
    public CarritoResponseDto agregarAlCarrito(CarritoRequestDto request) {
        Carrito carrito = carritoMapper.toDomain(request);

        // Verificar que los productos existan antes de guardar
        carrito.getItems().forEach(item ->
                productoClient.buscarPorId(item.getProductoId())
        );

        Carrito guardado = carritoRepository.save(carrito);
        return carritoMapper.toDto(guardado);
    }

    @Override
    public CarritoResponseDto obtenerCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        return carritoMapper.toDto(carrito);
    }
}

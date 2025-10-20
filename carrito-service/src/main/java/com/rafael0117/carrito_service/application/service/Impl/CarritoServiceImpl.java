package com.rafael0117.carrito_service.application.service.Impl;

import com.rafael0117.carrito_service.application.client.ProductoClient;
import com.rafael0117.carrito_service.application.client.ProductoResponseDto;

import com.rafael0117.carrito_service.application.service.CarritoService;
import com.rafael0117.carrito_service.domain.model.Carrito;
import com.rafael0117.carrito_service.domain.model.DetalleCarrito;
import com.rafael0117.carrito_service.domain.repository.CarritoRepository;
import com.rafael0117.carrito_service.domain.repository.DetalleCarritoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final DetalleCarritoRepository detalleRepository;
    private final ProductoClient productoClient;

    @Override
    public Carrito obtenerCarrito(Long idUsuario) {
        return carritoRepository.findByIdUsuario(idUsuario)
                .orElseGet(() -> carritoRepository.save(Carrito.builder()
                        .idUsuario(idUsuario)
                        .detalles(new ArrayList<>())
                        .build()));
    }

    @Override
    @Transactional
    public Carrito agregarProducto(Long idUsuario, DetalleCarrito detalleEntrada) {
        Carrito carrito = obtenerCarrito(idUsuario);
        if (carrito.getDetalles() == null) {
            carrito.setDetalles(new ArrayList<>());
        }

        // Consultar producto desde producto-service
        ProductoResponseDto producto = productoClient.buscarPorId(detalleEntrada.getIdProducto());
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado con ID: " + detalleEntrada.getIdProducto());
        }

        // Validar stock
        if (producto.getStock() < detalleEntrada.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        // Verificar si ya existe el producto en el carrito
        Optional<DetalleCarrito> existenteOpt = carrito.getDetalles().stream()
                .filter(d -> d.getIdProducto().equals(detalleEntrada.getIdProducto()))
                .findFirst();

        if (existenteOpt.isPresent()) {
            DetalleCarrito existente = existenteOpt.get();
            existente.setCantidad(existente.getCantidad() + detalleEntrada.getCantidad());
            detalleRepository.save(existente);
        } else {
            // Crear nuevo detalle
            DetalleCarrito nuevo = new DetalleCarrito();
            nuevo.setIdProducto(detalleEntrada.getIdProducto());
            nuevo.setCantidad(detalleEntrada.getCantidad());
            nuevo.setNombreProducto(producto.getNombre());
            nuevo.setPrecio(producto.getPrecio());
            nuevo.setCarrito(carrito);

            carrito.getDetalles().add(nuevo);
            detalleRepository.save(nuevo);
        }

        carritoRepository.save(carrito);
        return carritoRepository.findById(carrito.getId())
                .orElseThrow(() -> new RuntimeException("Error al actualizar el carrito"));
    }

    @Override
    @Transactional
    public void eliminarProducto(Long idUsuario, Long productoId) {
        Carrito carrito = carritoRepository.findByIdUsuario(idUsuario).orElse(null);
        if (carrito != null && carrito.getDetalles() != null) {
            carrito.getDetalles().removeIf(d -> d.getIdProducto().equals(productoId));
            carritoRepository.save(carrito);
        }
    }

    @Override
    public void vaciarCarrito(Long idUsuario) {
        Carrito carrito = obtenerCarrito(idUsuario);
        carrito.getDetalles().clear();
        carritoRepository.save(carrito);
    }
}
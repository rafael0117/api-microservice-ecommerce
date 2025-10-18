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

import java.util.List;
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
                        .idUsuario(idUsuario).build()));
    }

    @Override
    public Carrito agregarProducto(Long idUsuario, DetalleCarrito detalle) {
        // 1️⃣ Obtener el carrito del usuario (lo crea si no existe)
        Carrito carrito = obtenerCarrito(idUsuario);

        // 2️⃣ Consultar el producto desde producto-service usando Feign
        ProductoResponseDto producto = productoClient.buscarPorId(detalle.getIdProducto());
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado con ID: " + detalle.getIdProducto());
        }

        // 3️⃣ Validar stock
        if (producto.getStock() < detalle.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        // 4️⃣ Verificar si el producto ya está en el carrito
        Optional<DetalleCarrito> existente = carrito.getDetalles().stream()
                .filter(d -> d.getIdProducto().equals(detalle.getIdProducto()))
                .findFirst();

        if (existente.isPresent()) {
            // Si ya existe, sumamos la cantidad
            DetalleCarrito item = existente.get();
            item.setCantidad(item.getCantidad() + detalle.getCantidad());
        } else {
            // 5️⃣ Si es nuevo, agregamos los datos del producto al detalle
            detalle.setNombreProducto(producto.getNombre());
            detalle.setPrecio(producto.getPrecio());
            detalle.setCarrito(carrito);

            carrito.getDetalles().add(detalle);
        }

        // 6️⃣ Guardar carrito completo (se guarda en cascada si está configurado)
        carritoRepository.save(carrito);

        // 7️⃣ Devolver carrito actualizado
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

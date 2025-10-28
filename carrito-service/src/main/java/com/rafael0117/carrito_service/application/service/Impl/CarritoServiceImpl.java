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
import java.util.Objects;
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

        // 1) Obtener producto desde producto-service
        ProductoResponseDto producto = productoClient.buscarPorId(detalleEntrada.getIdProducto());
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado con ID: " + detalleEntrada.getIdProducto());
        }

        // 2) Validaciones básicas
        if (detalleEntrada.getCantidad() == null || detalleEntrada.getCantidad() <= 0) {
            throw new RuntimeException("Cantidad inválida");
        }
        if (producto.getStock() < detalleEntrada.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        // 3) Validar talla / color seleccionados (si el producto los maneja)
        String tallaSel  = detalleEntrada.getTalla();
        String colorSel  = detalleEntrada.getColor();
        // Normaliza a minúsculas para comparar (opcional)
        String tallaSelNorm = tallaSel != null ? tallaSel.trim().toLowerCase() : null;
        String colorSelNorm = colorSel != null ? colorSel.trim().toLowerCase() : null;

        if (producto.getTalla() != null && !producto.getTalla().isEmpty()) {
            boolean tallaOk = tallaSel != null && producto.getTalla().stream()
                    .anyMatch(t -> t != null && t.trim().equalsIgnoreCase(tallaSel));
            if (!tallaOk) throw new RuntimeException("Talla no válida para el producto");
        }
        if (producto.getColor() != null && !producto.getColor().isEmpty()) {
            boolean colorOk = colorSel != null && producto.getColor().stream()
                    .anyMatch(c -> c != null && c.trim().equalsIgnoreCase(colorSel));
            if (!colorOk) throw new RuntimeException("Color no válido para el producto");
        }

        // 4) Buscar si ya existe MISMA variante (idProducto + talla + color)
        Optional<DetalleCarrito> existenteOpt = carrito.getDetalles().stream()
                .filter(d -> d.getIdProducto().equals(detalleEntrada.getIdProducto())
                        && Objects.equals(
                        d.getTalla() == null ? null : d.getTalla().trim().toLowerCase(),
                        tallaSelNorm)
                        && Objects.equals(
                        d.getColor() == null ? null : d.getColor().trim().toLowerCase(),
                        colorSelNorm))
                .findFirst();

        if (existenteOpt.isPresent()) {
            // 5) Sumar cantidades a la MISMA variante
            DetalleCarrito existente = existenteOpt.get();
            existente.setCantidad(existente.getCantidad() + detalleEntrada.getCantidad());
            detalleRepository.save(existente);
        } else {
            // 6) Crear nuevo detalle SOLO con la selección
            DetalleCarrito nuevo = new DetalleCarrito();
            nuevo.setIdProducto(producto.getId());
            nuevo.setNombreProducto(producto.getNombre());      // snapshot
            nuevo.setDescripcion(producto.getDescripcion());    // snapshot corto si quieres
            nuevo.setPrecio(producto.getPrecio());
            nuevo.setCantidad(detalleEntrada.getCantidad());
            nuevo.setTalla(tallaSel);                           // <-- selección del usuario
            nuevo.setColor(colorSel);                           // <-- selección del usuario
            // imagen principal snapshot (no guardes todas)
            String imgPrincipal = (producto.getImagenesBase64() != null && !producto.getImagenesBase64().isEmpty())
                    ? producto.getImagenesBase64().get(0)
                    : null;

            nuevo.setCategoriaId(producto.getCategoriaId());
            nuevo.setCategoriaNombre(producto.getCategoriaNombre());
            nuevo.setCarrito(carrito);

            carrito.getDetalles().add(nuevo);
            detalleRepository.save(nuevo);
        }

        carritoRepository.save(carrito);
        // Devuelve el carrito actualizado (si quieres evitar lazy, usa fetch join o mapea a DTO)
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

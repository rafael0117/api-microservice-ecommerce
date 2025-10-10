package com.rafael0117.producto_service.web.controller;

import com.rafael0117.producto_service.application.service.ProductoService;
import com.rafael0117.producto_service.domain.model.Categoria;
import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> listar(){
        return ResponseEntity.ok(productoService.listar());
    }
    @PostMapping
    public ResponseEntity<ProductoResponseDto> guardar(@RequestBody ProductoRequestDto productoRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardar(productoRequestDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }
}

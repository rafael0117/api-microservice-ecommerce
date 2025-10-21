package com.rafael0117.producto_service.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafael0117.producto_service.application.service.ProductoService;

import com.rafael0117.producto_service.web.dto.producto.ProductoRequestDto;
import com.rafael0117.producto_service.web.dto.producto.ProductoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:62958")
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> listar(){
        return ResponseEntity.ok(productoService.listar());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductoResponseDto> actualizar(@PathVariable Long id,
                                                          @RequestBody ProductoRequestDto dto) {
        return ResponseEntity.ok(productoService.actualizar(id, dto));
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductoResponseDto> crear(@RequestBody ProductoRequestDto dto) {
        ProductoResponseDto respuesta = productoService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id); // Llama al método del service
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }

}

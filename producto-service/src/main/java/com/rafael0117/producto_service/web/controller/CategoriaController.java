package com.rafael0117.producto_service.web.controller;

import com.rafael0117.producto_service.application.service.CategoriaService;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaRequestDto;
import com.rafael0117.producto_service.web.dto.categoria.CategoriaResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDto>>listar(){
        return ResponseEntity.ok(service.listar());
    }
    @PostMapping
    public ResponseEntity<CategoriaResponseDto> guardar(@RequestBody CategoriaRequestDto categoriaRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(categoriaRequestDto));
    }
}

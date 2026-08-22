package com.negocio.pedidos.controller;

import com.negocio.pedidos.model.Producto;
import com.negocio.pedidos.service.ProductoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public Page<Producto> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return productoService.listarActivos(page, size);
    }

    @GetMapping("/buscar")
    public Page<Producto> buscar(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return productoService.buscar(q, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody NuevoProductoRequest request) {
        return productoService.crear(
            request.nombre(),
            request.categoria(),
            request.costo(),
            request.precio()
        );
    }

    @PutMapping("/{id}")
    public Producto actualizar(
            @PathVariable UUID id, 
            @Valid @RequestBody ActualizarProductoRequest request) {
        return productoService.actualizar(
            id,
            request.nombre(),
            request.categoria(),
            request.costo(),
            request.precio()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        productoService.eliminar(id);
    }
    
    @PatchMapping("/{id}/reactivar")
    public Producto reactivar(@PathVariable UUID id) {
        return productoService.reactivar(id);
    }

    public record NuevoProductoRequest(
        @NotBlank String nombre,
        String categoria,
        @NotNull BigDecimal costo,
        @NotNull BigDecimal precio
    ) {}

    public record ActualizarProductoRequest(
        @NotBlank String nombre,
        String categoria,
        @NotNull BigDecimal costo,
        @NotNull BigDecimal precio
    ) {}
}
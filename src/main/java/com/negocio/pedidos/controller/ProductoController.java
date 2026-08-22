package com.negocio.pedidos.controller;

import com.negocio.pedidos.model.Producto;
import com.negocio.pedidos.repository.ProductoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoRepository productoRepository;

    @GetMapping
    public List<Producto> listar() {
        return productoRepository.findByActivoTrue();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody NuevoProductoRequest request) {
        Producto producto = Producto.builder()
            .nombre(request.nombre())
            .categoria(request.categoria())
            .costo(request.costo())
            .precio(request.precio())
            .activo(true)
            .build();
        return productoRepository.save(producto);
    }

    @PutMapping("/{id}")
    public Producto actualizar(
            @PathVariable UUID id, 
            @Valid @RequestBody ActualizarProductoRequest request) {
        
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        
        producto.setNombre(request.nombre());
        producto.setCategoria(request.categoria());
        producto.setCosto(request.costo());
        producto.setPrecio(request.precio());
        
        return productoRepository.save(producto);
    }

    // "Eliminar" = desactivar, no borrar de verdad — así los pedidos ya
    // hechos con este producto conservan su historial intacto.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }
    
    @PatchMapping("/{id}/reactivar")
    public Producto reactivar(@PathVariable UUID id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));
        producto.setActivo(true);
        return productoRepository.save(producto);
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

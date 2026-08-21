package com.negocio.pedidos.controller;

import com.negocio.pedidos.model.Producto;
import com.negocio.pedidos.repository.ProductoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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

    public record NuevoProductoRequest(
        @NotBlank String nombre,
        String categoria,
        @NotNull BigDecimal costo,
        @NotNull BigDecimal precio
    ) {}
}

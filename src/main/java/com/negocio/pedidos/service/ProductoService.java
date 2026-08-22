package com.negocio.pedidos.service;

import com.negocio.pedidos.model.Producto;
import com.negocio.pedidos.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Listar con paginación
    public Page<Producto> listarActivos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return productoRepository.findByActivoTrue(pageable);
    }

    // Buscar con paginación
    public Page<Producto> buscar(String termino, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        
        if (termino == null || termino.trim().isEmpty()) {
            return productoRepository.findByActivoTrue(pageable);
        }

        String busqueda = "%" + termino.trim().toLowerCase() + "%";
        
        return productoRepository.buscarPorNombreOCategoria(busqueda, pageable);
    }

    @Transactional
    public Producto crear(String nombre, String categoria, BigDecimal costo, BigDecimal precio) {
        Producto producto = Producto.builder()
            .nombre(nombre)
            .categoria(categoria)
            .costo(costo)
            .precio(precio)
            .activo(true)
            .build();
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(UUID id, String nombre, String categoria, BigDecimal costo, BigDecimal precio) {
        Producto producto = findById(id);
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setCosto(costo);
        producto.setPrecio(precio);
        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminar(UUID id) {
        Producto producto = findById(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Transactional
    public Producto reactivar(UUID id) {
        Producto producto = findById(id);
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    private Producto findById(UUID id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));
    }
}
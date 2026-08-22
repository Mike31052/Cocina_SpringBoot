package com.negocio.pedidos.repository;

import com.negocio.pedidos.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {

    Page<Producto> findByActivoTrue(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "AND (LOWER(p.nombre) LIKE :busqueda OR LOWER(p.categoria) LIKE :busqueda)")
    Page<Producto> buscarPorNombreOCategoria(
        @Param("busqueda") String busqueda, 
        Pageable pageable
    );

}
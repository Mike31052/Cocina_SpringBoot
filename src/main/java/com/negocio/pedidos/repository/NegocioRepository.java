package com.negocio.pedidos.repository;

import com.negocio.pedidos.model.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NegocioRepository extends JpaRepository<Negocio, UUID> {
    List<Negocio> findByNombreContainingIgnoreCase(String texto);
}

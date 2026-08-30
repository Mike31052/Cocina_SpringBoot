package com.negocio.pedidos.repository;

import com.negocio.pedidos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByUsernameAndActivoTrue(String username);
}

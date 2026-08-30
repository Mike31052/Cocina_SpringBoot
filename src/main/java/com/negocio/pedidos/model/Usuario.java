package com.negocio.pedidos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Por ahora no hay pantalla de administración de usuarios: se crean
 * directamente en la base de datos (ver README de pedidos-backend para
 * cómo generar el hash del password). El campo "password" siempre
 * guarda el hash (BCrypt), nunca texto plano.
 */
@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 60)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @PrePersist
    void prePersist() {
        creadoEn = Instant.now();
    }
}

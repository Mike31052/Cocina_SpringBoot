package com.negocio.pedidos.model;

/**
 * Rol asignado a cada usuario. Determina qué pantallas y endpoints
 * puede usar dentro de la app (ver SecurityConfig).
 */
public enum Rol {
    VENDEDOR,
    ADMINISTRADOR
}

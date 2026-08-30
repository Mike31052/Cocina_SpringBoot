package com.negocio.pedidos.dto;

import com.negocio.pedidos.model.Rol;

public record LoginResponse(
    String token,
    String username,
    String nombre,
    Rol rol
) {}

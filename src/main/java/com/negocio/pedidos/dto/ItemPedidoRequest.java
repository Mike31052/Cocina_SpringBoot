package com.negocio.pedidos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ItemPedidoRequest(
    @NotNull UUID productoId,
    @Min(1) int cantidad,

    // Opcional: "sin salsa", "bien cocido", etc.
    @Size(max = 255) String nota
) {}
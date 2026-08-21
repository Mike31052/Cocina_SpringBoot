package com.negocio.pedidos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ItemPedidoRequest(
    @NotNull UUID productoId,
    @Min(1) int cantidad
) {}

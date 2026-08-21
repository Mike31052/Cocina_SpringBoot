package com.negocio.pedidos.dto;

import com.negocio.pedidos.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(
    @NotNull EstadoPedido estado
) {}

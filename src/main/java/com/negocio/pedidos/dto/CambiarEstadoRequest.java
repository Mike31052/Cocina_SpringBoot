package com.negocio.pedidos.dto;

import com.negocio.pedidos.model.EstadoPedido;
import com.negocio.pedidos.model.MetodoPago;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(
    @NotNull EstadoPedido estado,

    // Opcional: Administración lo manda al marcar como pagado (o para
    // corregirlo). Si el pedido ya tenía uno elegido por el vendedor,
    // este lo reemplaza.
    MetodoPago metodoPago
) {}

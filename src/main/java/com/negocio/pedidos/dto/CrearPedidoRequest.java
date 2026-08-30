package com.negocio.pedidos.dto;

import com.negocio.pedidos.model.MetodoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CrearPedidoRequest(
    @NotNull UUID negocioId,

    // Generado en el celular del vendedor antes de enviar.
    @NotNull UUID idLocalCelular,

    @NotEmpty @Valid List<ItemPedidoRequest> items,

    // Opcional: el vendedor lo puede elegir desde el inicio, pero el
    // pedido se queda igual en estatus RECIBIDO (pendiente).
    MetodoPago metodoPago
) {}

package com.negocio.pedidos.dto;

import com.negocio.pedidos.model.EstadoPedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
    UUID id,
    UUID negocioId,
    String nombreNegocio,
    EstadoPedido estado,
    Instant creadoEn,
    Instant actualizadoEn,
    List<PedidoDetalleResponse> items,
    BigDecimal total
) {}

package com.negocio.pedidos.dto;

import com.negocio.pedidos.model.EstadoPedido;
import com.negocio.pedidos.model.MetodoPago;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
    UUID id,
    UUID negocioId,
    String nombreNegocio,
    EstadoPedido estado,
    MetodoPago metodoPago,
    String vendedorUsername,
    Instant creadoEn,
    Instant actualizadoEn,
    List<PedidoDetalleResponse> items,
    BigDecimal total
) {}

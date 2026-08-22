package com.negocio.pedidos.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PedidoDetalleResponse(
    UUID productoId,
    String nombreProducto,
    int cantidad,
    BigDecimal precioUnitario,
    BigDecimal costoUnitario,
    String nota
) {}
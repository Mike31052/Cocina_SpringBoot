package com.negocio.pedidos.dto;

import java.math.BigDecimal;

public record TotalesDelDiaResponse(
    BigDecimal ventas,
    BigDecimal costos,
    BigDecimal ganancia,
    long pedidosCobrados
) {}

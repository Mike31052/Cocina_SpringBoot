package com.negocio.pedidos.model;

/**
 * Solo se guarda el dato de cómo se cobró el pedido; no se procesa
 * ningún cobro ni transferencia real desde el sistema.
 */
public enum MetodoPago {
    EFECTIVO,
    TRANSFERENCIA
}

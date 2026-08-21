package com.negocio.pedidos.model;

/**
 * Ciclo de vida de un pedido, en el orden en que avanza normalmente.
 * El vendedor solo crea pedidos en estado RECIBIDO; el resto de las
 * transiciones las hace Administracion.
 */
public enum EstadoPedido {
    RECIBIDO,
    REVISADO,
    LISTO_PARA_ENTREGAR,
    ENTREGADO,
    ENTREGADO_Y_PAGADO
}

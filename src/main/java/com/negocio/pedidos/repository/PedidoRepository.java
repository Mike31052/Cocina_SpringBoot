package com.negocio.pedidos.repository;

import com.negocio.pedidos.model.EstadoPedido;
import com.negocio.pedidos.model.Pedido;
import com.negocio.pedidos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    // Clave para la idempotencia: antes de crear, se busca si ya existe
    // un pedido con este id generado en el celular.
    Optional<Pedido> findByIdLocalCelular(UUID idLocalCelular);

    // Bandeja de Administracion: siempre acotada a un rango de fechas
    // (por defecto "hoy", pero puede ser cualquier rango) para no traer
    // cada vez mas registros conforme crece el historial.
    List<Pedido> findByCreadoEnBetweenOrderByCreadoEnDesc(Instant desde, Instant hasta);

    List<Pedido> findByEstadoAndCreadoEnBetweenOrderByCreadoEnDesc(
        EstadoPedido estado, Instant desde, Instant hasta
    );

    List<Pedido> findByEstadoAndActualizadoEnBetween(
        EstadoPedido estado, Instant desde, Instant hasta
    );

    // "Mis pedidos" del vendedor: mismo criterio, acotado por fecha.
    List<Pedido> findByCreadoPorAndCreadoEnBetweenOrderByCreadoEnDesc(
        Usuario creadoPor, Instant desde, Instant hasta
    );
}

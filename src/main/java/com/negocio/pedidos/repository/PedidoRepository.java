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

    List<Pedido> findAllByOrderByCreadoEnDesc();

    List<Pedido> findByEstadoOrderByCreadoEnDesc(EstadoPedido estado);

    List<Pedido> findByEstadoAndActualizadoEnBetween(
        EstadoPedido estado, Instant desde, Instant hasta
    );

    // Usado por la pantalla "Mis pedidos" del vendedor.
    List<Pedido> findByCreadoPorOrderByCreadoEnDesc(Usuario creadoPor);
}

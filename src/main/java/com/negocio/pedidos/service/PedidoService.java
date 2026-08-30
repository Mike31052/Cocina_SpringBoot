package com.negocio.pedidos.service;

import com.negocio.pedidos.dto.*;
import com.negocio.pedidos.model.*;
import com.negocio.pedidos.repository.NegocioRepository;
import com.negocio.pedidos.repository.PedidoRepository;
import com.negocio.pedidos.repository.ProductoRepository;
import com.negocio.pedidos.security.UsuarioPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final NegocioRepository negocioRepository;
    private final ProductoRepository productoRepository;
    private final NotificacionService notificacionService;

    /**
     * Crea un pedido, o si idLocalCelular ya existe, regresa el que ya
     * estaba guardado sin duplicarlo. Esto es lo que hace seguro reintentar
     * el envio desde el celular cuantas veces haga falta.
     */
    @Transactional
    public PedidoResponse crearPedido(CrearPedidoRequest request) {
        var existente = pedidoRepository.findByIdLocalCelular(request.idLocalCelular());
        if (existente.isPresent()) {
            return aRespuesta(existente.get());
        }

        Negocio negocio = negocioRepository.findById(request.negocioId())
            .orElseThrow(() -> new EntityNotFoundException("Negocio no encontrado: " + request.negocioId()));

        Pedido pedido = Pedido.builder()
            .negocio(negocio)
            .idLocalCelular(request.idLocalCelular())
            .estado(EstadoPedido.RECIBIDO)
            .metodoPago(request.metodoPago())
            .creadoPor(usuarioAutenticadoOrNull())
            .build();

        for (ItemPedidoRequest item : request.items()) {
            Producto producto = productoRepository.findById(item.productoId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + item.productoId()));

            // Se copia precio y costo actuales del producto: el pedido queda
            // con una "foto" de esos valores, no una referencia que cambie despues.
            PedidoDetalle detalle = PedidoDetalle.builder()
                .producto(producto)
                .cantidad(item.cantidad())
                .precioUnitario(producto.getPrecio())
                .costoUnitario(producto.getCosto())
                .nota(item.nota())
                .build();

            pedido.agregarDetalle(detalle);
        }

        Pedido guardado = pedidoRepository.save(pedido);
        PedidoResponse respuesta = aRespuesta(guardado);
        notificacionService.notificarPedidoNuevo(respuesta);
        return respuesta;
    }

    @Transactional
    public PedidoResponse cambiarEstado(UUID pedidoId, EstadoPedido nuevoEstado, MetodoPago metodoPago) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado: " + pedidoId));

        if (metodoPago != null) {
            pedido.setMetodoPago(metodoPago);
        }

        // Al marcar como pagado, el metodo de pago ya debe estar definido
        // (el vendedor lo pudo elegir desde el inicio, o lo manda ahora
        // Administracion en esta misma peticion).
        if (nuevoEstado == EstadoPedido.ENTREGADO_Y_PAGADO && pedido.getMetodoPago() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Debes indicar el método de pago para marcar el pedido como pagado."
            );
        }

        pedido.setEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);

        PedidoResponse respuesta = aRespuesta(actualizado);
        notificacionService.notificarCambioEstado(respuesta);
        return respuesta;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> bandeja(EstadoPedido estado) {
        List<Pedido> pedidos = (estado == null)
            ? pedidoRepository.findAllByOrderByCreadoEnDesc()
            : pedidoRepository.findByEstadoOrderByCreadoEnDesc(estado);

        return pedidos.stream().map(this::aRespuesta).toList();
    }

    /**
     * Pedidos levantados por el vendedor autenticado (pantalla "Mis pedidos").
     */
    @Transactional(readOnly = true)
    public List<PedidoResponse> misPedidos() {
        Usuario usuario = usuarioAutenticadoOrNull();
        if (usuario == null) {
            return List.of();
        }
        return pedidoRepository.findByCreadoPorOrderByCreadoEnDesc(usuario).stream()
            .map(this::aRespuesta)
            .toList();
    }

    private Usuario usuarioAutenticadoOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioPrincipal principal)) {
            return null;
        }
        return principal.getUsuario();
    }

    @Transactional(readOnly = true)
    public TotalesDelDiaResponse totalesDelDia() {
        ZoneId zona = ZoneOffset.UTC; 
        Instant inicioDelDia = LocalDate.now(zona).atStartOfDay(zona).toInstant();
        Instant ahora = Instant.now();

        List<Pedido> cobrados = pedidoRepository.findByEstadoAndActualizadoEnBetween(
            EstadoPedido.ENTREGADO_Y_PAGADO, inicioDelDia, ahora
        );

        BigDecimal ventas = BigDecimal.ZERO;
        BigDecimal costos = BigDecimal.ZERO;

        for (Pedido pedido : cobrados) {
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                BigDecimal cantidad = BigDecimal.valueOf(detalle.getCantidad());
                ventas = ventas.add(detalle.getPrecioUnitario().multiply(cantidad));
                costos = costos.add(detalle.getCostoUnitario().multiply(cantidad));
            }
        }

        return new TotalesDelDiaResponse(ventas, costos, ventas.subtract(costos), cobrados.size());
    }

    private PedidoResponse aRespuesta(Pedido pedido) {
        List<PedidoDetalleResponse> items = pedido.getDetalles().stream()
            .map(d -> new PedidoDetalleResponse(
                d.getProducto().getId(),
                d.getProducto().getNombre(),
                d.getCantidad(),
                d.getPrecioUnitario(),
                d.getCostoUnitario(),
                d.getNota()
            ))
            .toList();

        BigDecimal total = items.stream()
            .map(i -> i.precioUnitario().multiply(BigDecimal.valueOf(i.cantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PedidoResponse(
            pedido.getId(),
            pedido.getNegocio().getId(),
            pedido.getNegocio().getNombre(),
            pedido.getEstado(),
            pedido.getMetodoPago(),
            pedido.getCreadoPor() != null ? pedido.getCreadoPor().getUsername() : null,
            pedido.getCreadoEn(),
            pedido.getActualizadoEn(),
            items,
            total
        );
    }
}
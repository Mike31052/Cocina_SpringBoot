package com.negocio.pedidos.controller;

import com.negocio.pedidos.dto.*;
import com.negocio.pedidos.model.EstadoPedido;
import com.negocio.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // Usado por la app del Vendedor. Es seguro reintentar esta llamada
    // las veces que haga falta: el idLocalCelular evita duplicados.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crear(@Valid @RequestBody CrearPedidoRequest request) {
        return pedidoService.crearPedido(request);
    }

    // Usado por la app de Administracion para la bandeja. desde/hasta son
    // obligatorios (el front por defecto manda el rango de "hoy") para no
    // traer cada vez mas registros conforme crece el historial; estado es
    // opcional para filtrar dentro de ese rango.
    @GetMapping
    public List<PedidoResponse> listar(
        @RequestParam Instant desde,
        @RequestParam Instant hasta,
        @RequestParam(required = false) EstadoPedido estado
    ) {
        return pedidoService.bandeja(estado, desde, hasta);
    }

    // Usado por la app del Vendedor para ver los pedidos que el mismo
    // levanto, tambien acotado a un rango de fechas.
    @GetMapping("/mios")
    public List<PedidoResponse> misPedidos(
        @RequestParam Instant desde,
        @RequestParam Instant hasta
    ) {
        return pedidoService.misPedidos(desde, hasta);
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponse cambiarEstado(
        @PathVariable UUID id,
        @Valid @RequestBody CambiarEstadoRequest request
    ) {
        return pedidoService.cambiarEstado(id, request.estado(), request.metodoPago());
    }

    @GetMapping("/totales-hoy")
    public TotalesDelDiaResponse totalesHoy() {
        return pedidoService.totalesDelDia();
    }
}

package com.negocio.pedidos.controller;

import com.negocio.pedidos.dto.*;
import com.negocio.pedidos.model.EstadoPedido;
import com.negocio.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    // Usado por la app de Administracion para la bandeja en vivo.
    // ?estado=RECIBIDO filtra; sin parametro trae todos, mas recientes primero.
    @GetMapping
    public List<PedidoResponse> listar(@RequestParam(required = false) EstadoPedido estado) {
        return pedidoService.bandeja(estado);
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponse cambiarEstado(
        @PathVariable UUID id,
        @Valid @RequestBody CambiarEstadoRequest request
    ) {
        return pedidoService.cambiarEstado(id, request.estado());
    }

    @GetMapping("/totales-hoy")
    public TotalesDelDiaResponse totalesHoy() {
        return pedidoService.totalesDelDia();
    }
}

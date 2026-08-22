package com.negocio.pedidos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.negocio.pedidos.config.PedidosWebSocketHandler;
import com.negocio.pedidos.dto.PedidoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Avisa a Administracion cuando hay un pedido nuevo o cambia de estado.
 *
 * Hoy solo usa WebSocket plano (tiempo real mientras la app esta abierta).
 * Fase 2: agregar Firebase Cloud Messaging aqui mismo, para que la
 * notificacion tambien llegue con la app cerrada o el celular bloqueado.
 * El resto del sistema no se entera de ese cambio: solo se agrega una
 * llamada mas dentro de estos metodos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final PedidosWebSocketHandler pedidosWebSocketHandler;
    private final ObjectMapper objectMapper;

    public void notificarPedidoNuevo(PedidoResponse pedido) {
        enviar(pedido);
    }

    public void notificarCambioEstado(PedidoResponse pedido) {
        enviar(pedido);
    }

    private void enviar(PedidoResponse pedido) {
        try {
            String json = objectMapper.writeValueAsString(pedido);
            pedidosWebSocketHandler.enviarATodos(json);
        } catch (Exception e) {
            log.warn("No se pudo enviar la notificacion por WebSocket", e);
        }
    }
}

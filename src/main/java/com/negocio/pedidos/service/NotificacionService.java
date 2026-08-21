package com.negocio.pedidos.service;

import com.negocio.pedidos.dto.PedidoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Avisa a Administracion cuando hay un pedido nuevo o cambia de estado.
 *
 * Hoy solo usa WebSocket (tiempo real mientras la app esta abierta).
 * Fase 2: agregar Firebase Cloud Messaging aqui mismo, para que la
 * notificacion tambien llegue con la app cerrada o el celular bloqueado,
 * igual que un mensaje de WhatsApp. El resto del sistema no se entera
 * de ese cambio: solo se agrega una llamada mas dentro de estos metodos.
 */
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private static final String TOPICO_PEDIDOS = "/topic/pedidos";

    private final SimpMessagingTemplate messagingTemplate;

    public void notificarPedidoNuevo(PedidoResponse pedido) {
        messagingTemplate.convertAndSend(TOPICO_PEDIDOS, pedido);
        // TODO fase 2: enviarPush(pedido, "Pedido nuevo de " + pedido.nombreNegocio());
    }

    public void notificarCambioEstado(PedidoResponse pedido) {
        messagingTemplate.convertAndSend(TOPICO_PEDIDOS, pedido);
    }
}

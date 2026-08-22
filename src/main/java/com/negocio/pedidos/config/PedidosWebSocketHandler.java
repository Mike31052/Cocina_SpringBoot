package com.negocio.pedidos.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocket plano, sin STOMP: cada pedido nuevo o cambio de estatus se
 * manda como JSON directo a todas las sesiones conectadas (en la practica,
 * la o las pantallas de Administracion que esten abiertas).
 *
 * Se evito STOMP a proposito: las librerias cliente de STOMP tienen
 * problemas conocidos y mal soportados dentro de React Native.
 */
@Component
public class PedidosWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sesiones = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sesiones.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sesiones.remove(session);
    }

    public void enviarATodos(String json) {
        for (WebSocketSession sesion : sesiones) {
            try {
                if (sesion.isOpen()) {
                    sesion.sendMessage(new TextMessage(json));
                }
            } catch (Exception e) {
                // La sesion probablemente ya se cerro; se limpia sola
                // en afterConnectionClosed en el siguiente ciclo.
            }
        }
    }
}

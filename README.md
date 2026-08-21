# pedidos-backend

Backend del sistema de pedidos: recibe lo que levanta la app del Vendedor,
lo guarda, y avisa a la app de Administracion en tiempo real.

## Requisitos
- Java 17
- Maven
- Docker (para levantar Postgres local; en Railway sera un servicio administrado)

## Correrlo en local

```bash
# 1. Base de datos local
docker compose up -d

# 2. Backend
mvn spring-boot:run
```

Queda escuchando en `http://localhost:8080`.

## Endpoints principales

| Metodo | Ruta                          | Para que sirve                                  |
|--------|-------------------------------|--------------------------------------------------|
| POST   | /api/negocios                 | Registrar un negocio/cliente                      |
| GET    | /api/negocios?buscar=texto    | Buscar negocio (pantalla "elegir negocio")         |
| POST   | /api/productos                | Dar de alta un producto (nombre, costo, precio)    |
| GET    | /api/productos                | Listar productos activos                           |
| POST   | /api/pedidos                  | Crear pedido (lo usa la app Vendedor)              |
| GET    | /api/pedidos?estado=RECIBIDO  | Bandeja de pedidos (lo usa Administracion)         |
| PATCH  | /api/pedidos/{id}/estado      | Avanzar el estatus del pedido                      |
| GET    | /api/pedidos/totales-hoy      | Ventas, costos y ganancia del dia                  |

## Tiempo real

La app de Administracion se conecta por WebSocket (STOMP + SockJS) a `/ws`
y escucha el topico `/topic/pedidos`. Ahi llega cada pedido nuevo y cada
cambio de estatus, sin necesidad de refrescar.

Pendiente para fase 2: notificaciones push (Firebase Cloud Messaging) para
que Administracion se entere aunque tenga la app cerrada o el celular
bloqueado. El lugar donde se conecta es `NotificacionService` — ahi se
agrega sin tocar el resto del sistema.

## Idempotencia del envio

Cada pedido que crea el Vendedor trae un `idLocalCelular`: un identificador
generado en el celular ANTES de enviarlo. Si la app reintenta el envio por
falta de senal, el backend reconoce ese id y regresa el pedido que ya
tenia guardado, en vez de crear uno duplicado.

## Siguiente paso sugerido

Con esto ya se puede probar el flujo completo con curl o Postman antes de
tocar las apps moviles. Cuando el modelo se sienta estable, cambiar
`ddl-auto: update` por migraciones con Flyway.

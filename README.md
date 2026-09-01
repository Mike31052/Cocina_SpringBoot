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

| Metodo | Ruta                          | Rol requerido       | Para que sirve                                  |
|--------|-------------------------------|---------------------|--------------------------------------------------|
| POST   | /api/auth/login                | público             | Login: regresa el token JWT y el rol del usuario  |
| POST   | /api/negocios                 | autenticado          | Registrar un negocio/cliente                      |
| GET    | /api/negocios?buscar=texto    | autenticado          | Buscar negocio (pantalla "elegir negocio")         |
| POST   | /api/productos                | ADMINISTRADOR        | Dar de alta un producto (nombre, costo, precio)    |
| GET    | /api/productos                | autenticado          | Listar productos activos                           |
| POST   | /api/pedidos                  | autenticado          | Crear pedido (lo usa la app Vendedor)              |
| GET    | /api/pedidos?desde=&hasta=&estado=  | ADMINISTRADOR | Bandeja de pedidos, acotada a un rango de fechas |
| GET    | /api/pedidos/mios?desde=&hasta=      | VENDEDOR      | Pedidos del vendedor autenticado, acotados a un rango de fechas |
| PATCH  | /api/pedidos/{id}/estado      | ADMINISTRADOR        | Avanzar el estatus del pedido (y/o fijar metodoPago) |
| GET    | /api/pedidos/totales-hoy      | ADMINISTRADOR        | Ventas, costos y ganancia del dia                  |

Todas las rutas autenticadas esperan el header `Authorization: Bearer <token>`
que regresa `/api/auth/login`.

## Login y usuarios (sin pantalla todavia)

No hay endpoint ni pantalla para crear usuarios: se insertan directo en la
tabla `usuarios` (Hibernate la crea sola con `ddl-auto: update` en cuanto
levantas el backend). Columnas: `username`, `password` (hash BCrypt, nunca
texto plano), `nombre`, `rol` (`VENDEDOR` o `ADMINISTRADOR`), `activo`.

1. Genera el hash del password que le vas a dar al empleado:
   ```bash
   mvn spring-boot:run "-Dspring-boot.run.arguments=--generar-hash=09dst0095d"
   ```
   Copia el hash que imprime (algo como `$2a$10$...`).

2. Insértalo en la base de datos:
   ```sql
   INSERT INTO usuarios (id, username, password, nombre, rol, activo, creado_en)
   VALUES (gen_random_uuid(), 'maria', '$2a$10$...pegaAquiElHash...', 'María', 'VENDEDOR', true, now());
   ```

3. Entrega usuario y password (en texto plano) a la empleada. Con eso ya
   puede entrar a la app y ve solo las pantallas de su rol.

## Metodo de pago

`Pedido` tiene un campo `metodoPago` (`EFECTIVO` o `TRANSFERENCIA`), opcional.
El vendedor lo puede mandar desde `POST /api/pedidos`, pero el pedido se
queda igual en `RECIBIDO`. Administracion lo puede fijar o corregir al
mandar `PATCH /api/pedidos/{id}/estado` con `metodoPago` en el body. Si
el estado nuevo es `ENTREGADO_Y_PAGADO` y el pedido no tiene metodo de
pago (ni ya guardado ni en esta misma peticion), el backend responde
400 — es obligatorio saber cómo se cobró antes de marcarlo como pagado.
Esto solo guarda el dato: no se procesa ningún cobro ni transferencia real.

## Bandeja y "Mis pedidos" por rango de fechas

`GET /api/pedidos` y `GET /api/pedidos/mios` ya no traen todo el historial:
`desde` y `hasta` son obligatorios (formato ISO-8601, ej.
`2026-08-30T06:00:00Z`) y filtran por `creadoEn`. Esto evita que la
consulta se vuelva cada vez más lenta conforme crece la cantidad de
pedidos — la app siempre manda por defecto el rango de "hoy", y deja
elegir otro día o un rango más amplio desde la pantalla. El rango máximo
permitido es un año (`hasta - desde <= 366 días`); si se manda un rango
mayor, o si falta alguno de los dos parámetros, el backend responde 400.

`GET /api/pedidos` sigue aceptando `estado` como filtro opcional dentro
de ese rango.

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

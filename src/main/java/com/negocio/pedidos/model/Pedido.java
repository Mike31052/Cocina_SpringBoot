package com.negocio.pedidos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "pedidos",
    uniqueConstraints = @UniqueConstraint(columnNames = "id_local_celular")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    /**
     * Generado en el celular del vendedor ANTES de enviar el pedido.
     * Si el envio se reintenta por falta de senal, este valor permite
     * al backend reconocer que ya lo tiene y no crear un duplicado.
     */
    @Column(name = "id_local_celular", nullable = false)
    private UUID idLocalCelular;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.RECIBIDO;

    /**
     * Solo se guarda el dato de cómo se cobró; puede quedar en null si
     * el vendedor todavía no lo elige, y se puede fijar o corregir hasta
     * el momento en que Administración marca el pedido como pagado.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", length = 20)
    private MetodoPago metodoPago;

    /**
     * Vendedor que levantó el pedido. Nullable para no romper pedidos
     * creados antes de que existiera el login.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PedidoDetalle> detalles = new ArrayList<>();

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        creadoEn = now;
        actualizadoEn = now;
    }

    @PreUpdate
    void preUpdate() {
        actualizadoEn = Instant.now();
    }

    public void agregarDetalle(PedidoDetalle detalle) {
        detalle.setPedido(this);
        detalles.add(detalle);
    }
}

package org.esfe.Modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// Oferta de una actividad en un destino: duración y precio base propios de esa combinación.
// Una misma actividad (p. ej. "Senderismo") puede durar y costar distinto según el destino.
@Entity
@Table(name = "DestinoActividad",
        uniqueConstraints = @UniqueConstraint(name = "UQ_DestinoActividad",
                columnNames = {"IdDestino", "IdActividad"}))
@Getter
@Setter
@NoArgsConstructor
public class DestinoActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDestinoActividad")
    private Integer idDestinoActividad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdDestino", nullable = false)
    private Destino destino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdActividad", nullable = false)
    private Actividad actividad;

    // CHK_DestinoActividad_Duracion: > 0
    @Column(name = "DuracionMinutos", nullable = false)
    private Integer duracionMinutos;

    // CHK_DestinoActividad_Precio: >= 0. Precio actual de referencia; la Reserva guarda
    // su propio PrecioTotal, así que cambiar este valor no altera reservas ya hechas.
    @Column(name = "PrecioBase", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;
}
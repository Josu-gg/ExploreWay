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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "HistorialReserva")
@Getter
@Setter
@NoArgsConstructor
public class HistorialReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdHistorial")
    private Integer idHistorial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdReserva", nullable = false, updatable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdGuiaAnterior", updatable = false)
    private Guia guiaAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdGuiaNuevo", updatable = false)
    private Guia guiaNuevo;

    @Column(name = "Motivo", nullable = false, length = 500, updatable = false)
    private String motivo;

    @Column(name = "FechaCambio", nullable = false, updatable = false)
    private LocalDateTime fechaCambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdUsuarioResponsable", updatable = false)
    private Usuario usuarioResponsable;
}

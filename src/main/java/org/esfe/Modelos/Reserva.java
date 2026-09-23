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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "Reserva")
@Getter
@Setter
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdReserva")
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdCliente", nullable = false)
    private Cliente cliente;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdGuia", nullable = false)
    private Guia guia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdDestinoActividad", nullable = false)
    private DestinoActividad destinoActividad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;

    @Column(name = "FechaRecorrido", nullable = false)
    private LocalDate fechaRecorrido;

    @Column(name = "HoraInicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "HoraFin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "CantidadPersonas", nullable = false)
    private Integer cantidadPersonas;

    @Column(name = "PrecioTotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

    @Column(name = "FechaReserva", nullable = false, updatable = false)
    private LocalDateTime fechaReserva;

    @Column(name = "Observaciones", length = 500)
    private String observaciones;
}
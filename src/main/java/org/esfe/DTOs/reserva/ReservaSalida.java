package org.esfe.DTOs.reserva;

import lombok.Getter;
import org.esfe.Modelos.DestinoActividad;
import org.esfe.Modelos.Reserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class ReservaSalida {

    private final Integer idReserva;
    private final Integer idCliente;
    private final String nombreCliente;
    private final String apellidoCliente;
    private final Integer idGuia;
    private final String nombreGuia;
    private final String apellidoGuia;
    private final Integer idDestinoActividad;
    private final String nombreDestino;
    private final String nombreActividad;
    private final LocalDate fechaRecorrido;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private final Integer cantidadPersonas;
    private final BigDecimal precioTotal;
    private final LocalDateTime fechaReserva;
    private final String observaciones;
    private final Integer idEstado;
    private final String nombreEstado;

    private ReservaSalida(Reserva r) {
        DestinoActividad da = r.getDestinoActividad();
        this.idReserva = r.getIdReserva();
        this.idCliente = r.getCliente().getIdCliente();
        this.nombreCliente = r.getCliente().getPersona().getNombre();
        this.apellidoCliente = r.getCliente().getPersona().getApellido();
        this.idGuia = r.getGuia().getId();
        this.nombreGuia = r.getGuia().getPersona().getNombre();
        this.apellidoGuia = r.getGuia().getPersona().getApellido();
        this.idDestinoActividad = da.getIdDestinoActividad();
        this.nombreDestino = da.getDestino().getNombre();
        this.nombreActividad = da.getActividad().getNombre();
        this.fechaRecorrido = r.getFechaRecorrido();
        this.horaInicio = r.getHoraInicio();
        this.horaFin = r.getHoraFin();
        this.cantidadPersonas = r.getCantidadPersonas();
        this.precioTotal = r.getPrecioTotal();
        this.fechaReserva = r.getFechaReserva();
        this.observaciones = r.getObservaciones();
        this.idEstado = r.getEstado().getId();
        this.nombreEstado = r.getEstado().getNombreEstado();
    }

    // Debe llamarse dentro de una transacción (las relaciones son LAZY).
    public static ReservaSalida desde(Reserva r) {
        return new ReservaSalida(r);
    }
}
package org.esfe.DTOs.disponibilidadguia;

import lombok.Getter;
import org.esfe.Modelos.DisponibilidadGuia;
import org.esfe.Modelos.Persona;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class DisponibilidadGuiaSalida {

    private final Integer idDisponibilidad;
    private final Integer idGuia;
    private final String nombreGuia;
    private final String apellidoGuia;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private final Integer idEstado;
    private final String nombreEstado;

    private DisponibilidadGuiaSalida(DisponibilidadGuia dg) {
        Persona persona = dg.getGuia().getPersona();
        this.idDisponibilidad = dg.getIdDisponibilidad();
        this.idGuia = dg.getGuia().getId();
        this.nombreGuia = persona.getNombre();
        this.apellidoGuia = persona.getApellido();
        this.fecha = dg.getFecha();
        this.horaInicio = dg.getHoraInicio();
        this.horaFin = dg.getHoraFin();
        this.idEstado = dg.getEstado().getId();
        this.nombreEstado = dg.getEstado().getNombreEstado();
    }

    // Debe llamarse dentro de una transacción.
    public static DisponibilidadGuiaSalida desde(DisponibilidadGuia dg) {
        return new DisponibilidadGuiaSalida(dg);
    }
}
package org.esfe.DTOs.guiaactividad;

import lombok.Getter;
import org.esfe.Modelos.Actividad;
import org.esfe.Modelos.GuiaActividad;
import org.esfe.Modelos.Persona;

// Solo nombre y apellido del guía: teléfono, dirección y fecha de nacimiento
// no se exponen en este recurso (se consulta como público).
@Getter
public class GuiaActividadSalida {

    private final Integer idGuiaActividad;
    private final Integer idGuia;
    private final String nombreGuia;
    private final String apellidoGuia;
    private final Integer idActividad;
    private final String nombreActividad;
    private final String dificultad;
    private final Integer idEstado;
    private final String nombreEstado;

    private GuiaActividadSalida(GuiaActividad ga) {
        Persona persona = ga.getGuia().getPersona();
        Actividad actividad = ga.getActividad();
        this.idGuiaActividad = ga.getIdGuiaActividad();
        this.idGuia = ga.getGuia().getId();
        this.nombreGuia = persona.getNombre();
        this.apellidoGuia = persona.getApellido();
        this.idActividad = actividad.getId();
        this.nombreActividad = actividad.getNombre();
        this.dificultad = actividad.getDificultad();
        this.idEstado = ga.getEstado().getId();
        this.nombreEstado = ga.getEstado().getNombreEstado();
    }

    // Debe llamarse dentro de una transacción (las relaciones son LAZY).
    public static GuiaActividadSalida desde(GuiaActividad ga) {
        return new GuiaActividadSalida(ga);
    }
}
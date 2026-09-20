package org.esfe.DTOs.destinoactividad;

import lombok.Getter;
import org.esfe.Modelos.Actividad;
import org.esfe.Modelos.DestinoActividad;

import java.math.BigDecimal;

// Respuesta con todo lo que la app necesita para mostrar la oferta:
// datos de la actividad + duración y precio según el destino.
@Getter
public class DestinoActividadSalida {

    private final Integer idDestinoActividad;
    private final Integer idDestino;
    private final String nombreDestino;
    private final Integer idActividad;
    private final String nombreActividad;
    private final String descripcionActividad;
    private final String dificultad;
    private final Integer duracionMinutos;
    private final BigDecimal precioBase;
    private final Integer idEstado;
    private final String nombreEstado;

    private DestinoActividadSalida(DestinoActividad da) {
        Actividad actividad = da.getActividad();
        this.idDestinoActividad = da.getIdDestinoActividad();
        this.idDestino = da.getDestino().getId();
        this.nombreDestino = da.getDestino().getNombre();
        this.idActividad = actividad.getId();
        this.nombreActividad = actividad.getNombre();
        this.descripcionActividad = actividad.getDescripcion();
        this.dificultad = actividad.getDificultad();
        this.duracionMinutos = da.getDuracionMinutos();
        this.precioBase = da.getPrecioBase();
        this.idEstado = da.getEstado().getId();
        this.nombreEstado = da.getEstado().getNombreEstado();
    }

    // Debe llamarse dentro de una transacción (las relaciones son LAZY).
    public static DestinoActividadSalida desde(DestinoActividad da) {
        return new DestinoActividadSalida(da);
    }
}
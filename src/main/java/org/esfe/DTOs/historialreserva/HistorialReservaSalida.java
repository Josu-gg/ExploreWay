package org.esfe.DTOs.historialreserva;

import lombok.Getter;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.HistorialReserva;
import org.esfe.Modelos.Usuario;

import java.time.LocalDateTime;

@Getter
public class HistorialReservaSalida {

    private final Integer idHistorial;
    private final Integer idReserva;
    private final Integer idGuiaAnterior;
    private final String nombreGuiaAnterior;
    private final Integer idGuiaNuevo;
    private final String nombreGuiaNuevo;
    private final String motivo;
    private final LocalDateTime fechaCambio;
    private final Integer idUsuarioResponsable;

    private HistorialReservaSalida(HistorialReserva h) {
        Usuario responsable = h.getUsuarioResponsable();
        this.idHistorial = h.getIdHistorial();
        this.idReserva = h.getReserva().getIdReserva();
        this.idGuiaAnterior = idDe(h.getGuiaAnterior());
        this.nombreGuiaAnterior = nombreDe(h.getGuiaAnterior());
        this.idGuiaNuevo = idDe(h.getGuiaNuevo());
        this.nombreGuiaNuevo = nombreDe(h.getGuiaNuevo());
        this.motivo = h.getMotivo();
        this.fechaCambio = h.getFechaCambio();
        this.idUsuarioResponsable = responsable == null ? null : responsable.getIdUsuario();
    }

    private static Integer idDe(Guia guia) {
        return guia == null ? null : guia.getId();
    }

    private static String nombreDe(Guia guia) {
        return guia == null ? null
                : guia.getPersona().getNombre() + " " + guia.getPersona().getApellido();
    }

    // Debe llamarse dentro de una transacción (las relaciones son LAZY).
    public static HistorialReservaSalida desde(HistorialReserva h) {
        return new HistorialReservaSalida(h);
    }
}
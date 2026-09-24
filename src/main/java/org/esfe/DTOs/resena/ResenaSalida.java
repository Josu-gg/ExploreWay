package org.esfe.DTOs.resena;

import lombok.Getter;
import org.esfe.Modelos.Resena;

import java.time.LocalDateTime;

@Getter
public class ResenaSalida {

    private final Integer idResena;
    private final Integer idReserva;
    private final Integer idCliente;
    private final String nombreCliente;
    private final String apellidoCliente;
    private final Integer idGuia;
    private final String nombreGuia;
    private final String apellidoGuia;
    private final Integer calificacion;
    private final String comentario;
    private final LocalDateTime fecha;

    private ResenaSalida(Resena r) {
        this.idResena = r.getIdResena();
        this.idReserva = r.getReserva().getIdReserva();
        this.idCliente = r.getCliente().getIdCliente();
        this.nombreCliente = r.getCliente().getPersona().getNombre();
        this.apellidoCliente = r.getCliente().getPersona().getApellido();
        this.idGuia = r.getGuia().getId();
        this.nombreGuia = r.getGuia().getPersona().getNombre();
        this.apellidoGuia = r.getGuia().getPersona().getApellido();
        this.calificacion = r.getCalificacion();
        this.comentario = r.getComentario();
        this.fecha = r.getFecha();
    }

    // Debe llamarse dentro de una transacción (las relaciones son LAZY).
    public static ResenaSalida desde(Resena r) {
        return new ResenaSalida(r);
    }
}
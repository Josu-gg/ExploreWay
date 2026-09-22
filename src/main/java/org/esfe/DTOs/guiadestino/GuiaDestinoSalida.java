package org.esfe.DTOs.guiadestino;

import lombok.Getter;
import org.esfe.Modelos.Destino;
import org.esfe.Modelos.GuiaDestino;
import org.esfe.Modelos.Persona;

// Solo nombre y apellido del guía: teléfono, dirección y fecha de nacimiento
// no se exponen en este recurso (se consulta como público).
@Getter
public class GuiaDestinoSalida {

    private final Integer idGuiaDestino;
    private final Integer idGuia;
    private final String nombreGuia;
    private final String apellidoGuia;
    private final Integer idDestino;
    private final String nombreDestino;
    private final String departamento;
    private final String municipio;
    private final Integer idEstado;
    private final String nombreEstado;

    private GuiaDestinoSalida(GuiaDestino gd) {
        Persona persona = gd.getGuia().getPersona();
        Destino destino = gd.getDestino();
        this.idGuiaDestino = gd.getIdGuiaDestino();
        this.idGuia = gd.getGuia().getId();
        this.nombreGuia = persona.getNombre();
        this.apellidoGuia = persona.getApellido();
        this.idDestino = destino.getId();
        this.nombreDestino = destino.getNombre();
        this.departamento = destino.getDepartamento();
        this.municipio = destino.getMunicipio();
        this.idEstado = gd.getEstado().getId();
        this.nombreEstado = gd.getEstado().getNombreEstado();
    }

    // Debe llamarse dentro de una transacción (las relaciones son LAZY).
    public static GuiaDestinoSalida desde(GuiaDestino gd) {
        return new GuiaDestinoSalida(gd);
    }
}

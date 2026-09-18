package org.esfe.DTOs.cliente;

import lombok.Builder;
import lombok.Getter;
import org.esfe.DTOs.persona.PersonaSalida;
import org.esfe.Modelos.Cliente;

@Getter
@Builder
public class ClienteSalida {

    private Integer idCliente;
    private Integer idEstado;
    private String nombreEstado;
    private PersonaSalida persona;

    // Debe llamarse dentro de una transacción (las relaciones son LAZY).
    public static ClienteSalida desde(Cliente c) {
        return ClienteSalida.builder()
                .idCliente(c.getIdCliente())
                .idEstado(c.getEstado().getIdEstado())
                .nombreEstado(c.getEstado().getNombreEstado())
                .persona(PersonaSalida.desde(c.getPersona()))
                .build();
    }
}
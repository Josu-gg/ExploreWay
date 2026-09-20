package org.esfe.DTOs.cliente;

import lombok.Getter;
import org.esfe.DTOs.persona.PersonaSalida;
import org.esfe.Modelos.Cliente;

@Getter
public class ClienteSalida extends PersonaSalida {

    private final Integer idCliente;
    private final Integer idEstado;
    private final String nombreEstado;

 private ClienteSalida(Cliente c){
     super(c.getPersona());
     this.idCliente = c.getIdCliente();
     this.idEstado = c.getEstado().getId();
     this.nombreEstado = c.getEstado().getNombreEstado();
 }

 public static ClienteSalida desde(Cliente c){
     return new ClienteSalida(c);
 }
}
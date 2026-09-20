package org.esfe.DTOs.persona;

import lombok.Getter;
import org.esfe.Modelos.Persona;

import java.time.LocalDate;

// Datos de Persona que comparten las respuestas de Persona, Usuario y Cliente (y luego Guia).
// Las subclases llaman a super(persona) desde su constructor.
@Getter
public class PersonaSalida {

    private final Integer idPersona;
    private final String nombre;
    private final String apellido;
    private final String telefono;
    private final String direccion;
    private final LocalDate fechaNacimiento;
    private final String foto;

    protected PersonaSalida(Persona persona) {
        this.idPersona = persona.getId();
        this.nombre = persona.getNombre();
        this.apellido = persona.getApellido();
        this.telefono = persona.getTelefono();
        this.direccion = persona.getDireccion();
        this.fechaNacimiento = persona.getFechaNacimiento();
        this.foto = persona.getFoto();
    }

    public static PersonaSalida desde(Persona persona) {
        return new PersonaSalida(persona);
    }
}

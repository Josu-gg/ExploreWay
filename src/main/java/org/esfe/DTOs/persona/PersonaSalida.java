package org.esfe.DTOs.persona;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaSalida {
    private Integer idPersona;
    private String nombre;
    private String apellido;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private String foto;
}

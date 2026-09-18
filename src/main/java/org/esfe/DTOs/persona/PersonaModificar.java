package org.esfe.DTOs.persona;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PersonaModificar {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    private String apellido;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 20, message = "El telefono no puede superar los 20 caracteres")
    private String telefono;

    @Size(max = 250, message = "La direccion no  puede superar los 250 caracteres")
    private String direccion;

    @Past(message = "La fecha de nacimiento debe ser anteior a la fecha actual")
    private LocalDate fechaNacimiento;

    @Size(max = 500, message = "La foto no puede superar los 500 caracteres")
    private String foto;
}

package org.esfe.DTOs.persona;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// Datos personales y validaciones comunes a todo lo que crea o modifica una Persona
// (Persona, Usuario, Cliente y luego Guia). Las demás clases de entrada heredan de aquí.
@Getter
@Setter
public abstract class PersonaDatos {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(max = 100, message = "El apellido no puede superar 100 caracteres.")
    private String apellido;

    @NotBlank(message = "El teléfono es obligatorio.")
    @Pattern(regexp = "^[0-9+\\-\\s]{8,20}$", message = "El teléfono solo admite números, +, - y espacios (8 a 20 caracteres).")
    private String telefono;

    @Size(max = 250, message = "La dirección no puede superar 250 caracteres.")
    private String direccion;

    @Past(message = "La fecha de nacimiento debe ser una fecha pasada.")
    private LocalDate fechaNacimiento;

    @Size(max = 500, message = "La URL de la foto no puede superar 500 caracteres.")
    private String foto;
}

package org.esfe.DTOs.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// Campos y validaciones comunes a crear y modificar un usuario.
@Getter
@Setter
public abstract class UsuarioDatos {

    // ── Datos de Persona ──────────────────────────────────────
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

    // ── Datos de Usuario ──────────────────────────────────────
    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres.")
    private String correo;

    @NotNull(message = "El rol es obligatorio.")
    @Positive(message = "El rol no es válido.")
    private Integer idRol;
}
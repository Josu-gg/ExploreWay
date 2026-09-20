package org.esfe.DTOs.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Datos comunes para crear una cuenta nueva: Persona + correo (heredados) + contraseña.
// La heredan UsuarioGuardar (crea un administrador, que además elige el rol) y
// ClienteGuardar (registro público, el rol lo fija el servidor).
// Sin @ToString/@Data a propósito: la contraseña no debe terminar en un log.
@Getter
@Setter
public abstract class UsuarioRegistroDatos extends UsuarioDatos {

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe incluir al menos una letra y un número.")
    private String contra;
}

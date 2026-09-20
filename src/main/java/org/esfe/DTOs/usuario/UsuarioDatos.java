package org.esfe.DTOs.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.esfe.DTOs.persona.PersonaDatos;

// Datos personales + correo: lo común a crear y modificar una cuenta de usuario.
@Getter
@Setter
public abstract class UsuarioDatos extends PersonaDatos {

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres.")
    private String correo;
}

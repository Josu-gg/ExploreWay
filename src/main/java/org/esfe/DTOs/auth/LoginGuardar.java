package org.esfe.DTOs.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Sin @ToString/@Data: la contraseña no debe terminar en un log.
@Getter
@Setter
public class LoginGuardar {
    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres.")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(max = 72, message = "La contraseña no puede superar 72 caracteres.")
    private String contra;
}

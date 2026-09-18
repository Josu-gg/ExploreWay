package org.esfe.DTOs.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Sin @Data/@ToString a propósito: así la contraseña nunca termina en un log por accidente.
@Getter
@Setter
public class UsuarioGuardar extends UsuarioDatos {

    // BCrypt solo procesa los primeros 72 bytes, por eso el máximo.
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe incluir al menos una letra y un número.")
    private String contra;
}
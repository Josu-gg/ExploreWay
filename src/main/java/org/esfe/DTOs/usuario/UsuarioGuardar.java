package org.esfe.DTOs.usuario;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// Creación de usuario por un administrador: el rol lo elige quien crea la cuenta.
@Getter
@Setter
public class UsuarioGuardar extends UsuarioRegistroDatos {

    @NotNull(message = "El rol es obligatorio.")
    @Positive(message = "El rol no es válido.")
    private Integer idRol;
}

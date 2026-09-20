package org.esfe.DTOs.usuario;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// No incluye la contraseña: el cambio de contraseña es una operación aparte
// (requiere validar la contraseña actual) y se hará junto con la autenticación.
@Getter
@Setter
public class UsuarioModificar extends UsuarioDatos {

    @NotNull(message = "El rol es obligatorio.")
    @Positive(message = "El rol no es válido.")
    private Integer idRol;

    // Permite activar o bloquear la cuenta (estados de tipo "Usuario").
    @NotNull(message = "El estado es obligatorio.")
    @Positive(message = "El estado no es válido.")
    private Integer idEstado;
}

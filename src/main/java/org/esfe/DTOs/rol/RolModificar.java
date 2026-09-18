package org.esfe.DTOs.rol;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolModificar {
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 50, message = "El nombre del rol no puede superar los 50 caracteres")
    private String nombreRol;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcionRol;
}

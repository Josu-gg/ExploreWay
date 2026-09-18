package org.esfe.DTOs.estado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class EstadoModificar {
    @NotBlank(message = "El nombre del estado es obligatorio")
    @Size(max = 50, message = "El nombre del estado no puede superar 50 caracteres")
    private String nombreEstado;

    @NotBlank(message = "El tipo de estado es obligatorio")
    @Size(max = 50, message = "El tipo de estado no puede superar 50 caracteres")
    private String tipoEstado;
}

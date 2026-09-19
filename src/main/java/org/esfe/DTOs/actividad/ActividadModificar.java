package org.esfe.DTOs.actividad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActividadModificar {
    @NotBlank(message = "El nombre de la actividad es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    private String descripcion;

    @Size(max = 50, message = "La dificultad no puede superar los 50 caracteres")
    private String dificultad;

    @NotNull(message = "El estado es obligatorio")
    private Integer idEstado;
}

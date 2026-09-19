package org.esfe.DTOs.destino;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinoModificar {
    @NotBlank(message = "El nombre del destino es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    private String descripcion;

    @NotBlank(message = "El departamento es obligatorio")
    @Size(max = 100, message = "El departamento no puede superar los 100 caracteres")
    private String departamento;

    @Size(max = 100, message = "El municipio no puede superar los 100 caracteres")
    private String municipio;

    @NotNull(message = "El estado es obligatorio")
    private Integer idEstado;
}

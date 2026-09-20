package org.esfe.DTOs.guia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuiaGuardar {
    @NotNull(message = "La persona asociada es obligatoria")
    private Integer idPersona;

    @Size(max = 500, message = "La biografía no puede superar los 500 caracteres")
    private String biografia;

    @Size(max = 500, message = "La experiencia no puede superar los 500 caracteres")
    private String experiencia;

    @Size(max = 200, message = "Los estudios no pueden superar los 200 caracteres")
    private String estudios;

    @NotNull(message = "Debe indicarse si tiene primeros auxilios")
    private Boolean primerosAuxilios;

    @NotNull(message = "Debe indicarse la disponibilidad")
    private Boolean estadoDisponibilidad;

    @NotNull(message = "El estado es obligatorio")
    private Integer idEstado;
}

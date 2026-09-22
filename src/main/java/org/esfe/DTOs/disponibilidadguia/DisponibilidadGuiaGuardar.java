package org.esfe.DTOs.disponibilidadguia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisponibilidadGuiaGuardar extends DisponibilidadGuiaDatos {

    @NotNull(message = "El guía es obligatorio.")
    @Positive(message = "El guía no es válido.")
    private Integer idGuia;
}
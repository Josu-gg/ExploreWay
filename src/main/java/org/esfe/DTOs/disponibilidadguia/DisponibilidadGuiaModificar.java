package org.esfe.DTOs.disponibilidadguia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// Para "eliminar" una franja con historial se cambia a un estado inactivo (baja lógica).
@Getter
@Setter
public class DisponibilidadGuiaModificar extends DisponibilidadGuiaDatos {

    @NotNull(message = "El estado es obligatorio.")
    @Positive(message = "El estado no es válido.")
    private Integer idEstado;
}
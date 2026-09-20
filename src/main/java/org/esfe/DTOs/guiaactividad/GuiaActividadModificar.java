package org.esfe.DTOs.guiaactividad;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// Solo cambia el estado (suspender o reactivar la asignación).
// Para otra combinación guía/actividad se crea una asignación nueva.
@Getter
@Setter
public class GuiaActividadModificar {

    @NotNull(message = "El estado es obligatorio.")
    @Positive(message = "El estado no es válido.")
    private Integer idEstado;
}
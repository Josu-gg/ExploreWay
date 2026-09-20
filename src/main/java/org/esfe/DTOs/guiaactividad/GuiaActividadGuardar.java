package org.esfe.DTOs.guiaactividad;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// No incluye idEstado: toda asignación nueva nace "Activo" (tipo General) y lo fija el servidor.
@Getter
@Setter
public class GuiaActividadGuardar {

    @NotNull(message = "El guía es obligatorio.")
    @Positive(message = "El guía no es válido.")
    private Integer idGuia;

    @NotNull(message = "La actividad es obligatoria.")
    @Positive(message = "La actividad no es válida.")
    private Integer idActividad;
}
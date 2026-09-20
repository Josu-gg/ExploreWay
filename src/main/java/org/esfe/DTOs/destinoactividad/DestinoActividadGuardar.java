package org.esfe.DTOs.destinoactividad;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// No incluye idEstado: toda oferta nueva nace "Activo" (tipo General) y lo fija el servidor.
@Getter
@Setter
public class DestinoActividadGuardar extends DestinoActividadDatos {

    @NotNull(message = "El destino es obligatorio.")
    @Positive(message = "El destino no es válido.")
    private Integer idDestino;

    @NotNull(message = "La actividad es obligatoria.")
    @Positive(message = "La actividad no es válida.")
    private Integer idActividad;
}
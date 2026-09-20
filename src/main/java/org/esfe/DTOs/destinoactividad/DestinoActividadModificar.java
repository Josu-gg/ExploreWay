package org.esfe.DTOs.destinoactividad;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// No permite cambiar destino ni actividad: las reservas existentes apuntan a esta oferta
// y cambiarle el par destino/actividad alteraría su significado. Para otra combinación, se crea una nueva.
// Para "eliminar" una oferta se cambia a un estado inactivo (baja lógica).
@Getter
@Setter
public class DestinoActividadModificar extends DestinoActividadDatos {

    @NotNull(message = "El estado es obligatorio.")
    @Positive(message = "El estado no es válido.")
    private Integer idEstado;
}
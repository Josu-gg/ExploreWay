package org.esfe.DTOs.comun;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// DTO compartido para recursos cuya única modificación permitida es el estado
// (suspender o reactivar), p. ej. GuiaActividad y GuiaDestino.
// Si un recurso necesita modificar más campos, debe tener su propio *Modificar.
@Getter
@Setter
public class CambioEstadoModificar {
    @NotNull(message = "El estado  es obligatorio")
    @Positive(message = "El Estado no es valido")
    private Integer idEstado;
}

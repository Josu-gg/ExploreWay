package org.esfe.DTOs.resena;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// No incluye idCliente ni idGuia: se toman de la reserva para que no puedan manipularse.
@Getter
@Setter
public class ResenaGuardar {

    @NotNull(message = "La reserva es obligatoria.")
    @Positive(message = "La reserva no es válida.")
    private Integer idReserva;

    @NotNull(message = "La calificación es obligatoria.")
    @Min(value = 1, message = "La calificación mínima es 1.")
    @Max(value = 5, message = "La calificación máxima es 5.")
    private Integer calificacion;

    @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres.")
    private String comentario;
}
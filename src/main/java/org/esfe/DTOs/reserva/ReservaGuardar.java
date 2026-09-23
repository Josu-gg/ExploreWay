package org.esfe.DTOs.reserva;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservaGuardar {

    @NotNull(message = "El cliente es obligatorio.")
    @Positive(message = "El cliente no es válido.")
    private Integer idCliente;

    @NotNull(message = "El guía es obligatorio.")
    @Positive(message = "El guía no es válido.")
    private Integer idGuia;

    @NotNull(message = "La actividad del destino es obligatoria.")
    @Positive(message = "La actividad del destino no es válida.")
    private Integer idDestinoActividad;

    @NotNull(message = "La fecha del recorrido es obligatoria.")
    @FutureOrPresent(message = "La fecha del recorrido no puede ser anterior a hoy.")
    private LocalDate fechaRecorrido;

    @NotNull(message = "La hora de inicio es obligatoria.")
    private LocalTime horaInicio;

    @NotNull(message = "La cantidad de personas es obligatoria.")
    @Positive(message = "La cantidad de personas debe ser mayor que cero.")
    private Integer cantidadPersonas;

    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres.")
    private String observaciones;
}
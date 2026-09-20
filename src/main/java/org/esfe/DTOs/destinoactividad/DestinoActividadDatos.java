package org.esfe.DTOs.destinoactividad;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// Campos y validaciones comunes a crear y modificar una oferta (mismo patrón que PersonaDatos).
@Getter
@Setter
public abstract class DestinoActividadDatos {

    // Una reserva ocurre en una sola fecha con HoraInicio < HoraFin,
    // así que la duración no puede pasar de 23 h 59 min.
    @NotNull(message = "La duración es obligatoria.")
    @Positive(message = "La duración debe ser mayor que 0 minutos.")
    @Max(value = 1439, message = "La duración no puede superar 1439 minutos (23 h 59 min).")
    private Integer duracionMinutos;

    // DECIMAL(10,2) en la BD: 8 enteros y 2 decimales.
    @NotNull(message = "El precio base es obligatorio.")
    @DecimalMin(value = "0.00", message = "El precio base no puede ser negativo.")
    @Digits(integer = 8, fraction = 2, message = "El precio base admite hasta 8 enteros y 2 decimales.")
    private BigDecimal precioBase;
}
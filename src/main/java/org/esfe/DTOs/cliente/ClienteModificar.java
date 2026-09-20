package org.esfe.DTOs.cliente;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.esfe.DTOs.persona.PersonaDatos;

// Modifica los datos personales del cliente y su estado (estados de tipo "General").
@Getter
@Setter
public class ClienteModificar extends PersonaDatos {

    @NotNull(message = "El estado es obligatorio.")
    @Positive(message = "El estado no es válido.")
    private Integer idEstado;
}

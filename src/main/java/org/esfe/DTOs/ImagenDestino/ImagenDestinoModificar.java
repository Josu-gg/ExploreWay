package org.esfe.DTOs.ImagenDestino;

import jakarta.validation.constraints.Size;

public record ImagenDestinoModificar(
        @Size(max = 255, message = "La descripcion no puede superar los 255 caracteres")
        String descripcion
) {}

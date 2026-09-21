package org.esfe.DTOs.ImagenDestino;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ImagenDestinoGuardar (
    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Size(max = 500, message = "La URL no puede superar los 500 caracteres")
    @URL(protocol = "https", message = "La URL debe ser valida y usar https")
    String urlImagen,

    @Size(max = 255, message = "La descripcion no puede superar los 255 caracteres")
    String descripcion,

    Boolean esPrincipal
){}



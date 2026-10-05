package org.esfe.DTOs.guia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Datos que el propio guía puede editar de su perfil (el estado lo gestiona el Administrador).
@Getter
@Setter
public class GuiaPerfilModificar {
    @Size(max = 500, message = "La biografía no puede superar los 500 caracteres")
    private String biografia;

    @Size(max = 500, message = "La experiencia no puede superar los 500 caracteres")
    private String experiencia;

    @Size(max = 200, message = "Los estudios no pueden superar los 200 caracteres")
    private String estudios;

    @NotNull(message = "Debe indicarse si tiene primeros auxilios")
    private Boolean primerosAuxilios;

    @NotNull(message = "Debe indicarse la disponibilidad")
    private Boolean estadoDisponibilidad;

    // URL de la foto de perfil (Cloudinary). null = sin cambios; vacío = quitar la foto.
    @Size(max = 500, message = "La URL de la foto no puede superar 500 caracteres")
    private String foto;
}

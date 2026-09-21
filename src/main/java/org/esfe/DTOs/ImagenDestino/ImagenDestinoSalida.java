package org.esfe.DTOs.ImagenDestino;

public record ImagenDestinoSalida(
        Integer idImagen,
        String urlImagen,
        String descripcion,
        Boolean esPrincipal
) {}

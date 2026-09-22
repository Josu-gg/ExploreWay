package org.esfe.DTOs.guiadestino;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// No incluye idEstado: toda asignación nueva nace "Activo" (tipo General) y lo fija el servidor.
@Getter
@Setter
public class GuiaDestinoGuardar {
    @NotNull(message = "El guia es obligatorio")
    @Positive(message = "El guia no es valido")
    private Integer idGuia;

    @NotNull(message = "El destino es obligatorio")
    @Positive(message = "El destino no es valido")
    private Integer idDestino;
}

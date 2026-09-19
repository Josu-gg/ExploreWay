package org.esfe.DTOs.destino;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DestinoSalida {
    private Integer idDestino;
    private String nombre;
    private String descripcion;
    private String departamento;
    private String municipio;
    private Integer idEstado;
    private String nombreEstado;
}

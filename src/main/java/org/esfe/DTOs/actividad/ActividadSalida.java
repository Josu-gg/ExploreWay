package org.esfe.DTOs.actividad;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActividadSalida {
    private Integer idActividad;
    private String nombre;
    private String descripcion;
    private String dificultad;
    private Integer idEstado;
    private String nombreEstado;
}

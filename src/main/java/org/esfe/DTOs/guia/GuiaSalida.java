package org.esfe.DTOs.guia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuiaSalida {
    private Integer idGuia;
    private Integer idPersona;
    private String nombrePersona;
    private String apellidoPersona;
    private String biografia;
    private String experiencia;
    private String estudios;
    private Boolean primerosAuxilios;
    private Boolean estadoDisponibilidad;
    private BigDecimal calificacionPromedio;
    private Integer idEstado;
    private String nombreEstado;
}

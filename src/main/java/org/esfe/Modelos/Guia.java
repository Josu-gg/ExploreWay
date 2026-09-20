package org.esfe.Modelos;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "Guia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Guia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdGuia")
    private Integer id;

    @NotNull(message = "La persona asociada es obligatoria")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPersona", nullable = false, unique = true)
    private Persona persona;

    @Size(max = 500, message = "La biografía no puede superar los 500 caracteres")
    @Column(name = "Biografia", length = 500)
    private String biografia;

    @Size(max = 500, message = "La experiencia no puede superar los 500 caracteres")
    @Column(name = "Experiencia", length = 500)
    private String experiencia;

    @Size(max = 200, message = "Los estudios no pueden superar los 200 caracteres")
    @Column(name = "Estudios", length = 200)
    private String estudios;

    @NotNull(message = "Debe indicarse si tiene primeros auxilios")
    @Column(name = "PrimerosAuxilios", nullable = false)
    private Boolean primerosAuxilios = false;

    @NotNull(message = "Debe indicarse la disponibilidad")
    @Column(name = "EstadoDisponibilidad", nullable = false)
    private Boolean estadoDisponibilidad = true;

    @NotNull(message = "La calificación promedio es obligatoria")
    @DecimalMin(value = "0.00", message = "La calificación no puede ser menor a 0")
    @DecimalMax(value = "5.00", message = "La calificación no puede ser mayor a 5")
    @Column(name = "CalificacionPromedio", precision = 3, scale = 2, nullable = false)
    private BigDecimal calificacionPromedio = BigDecimal.ZERO;

    @NotNull(message = "El estado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;
}

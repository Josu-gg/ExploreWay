package org.esfe.Modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Actividades que un guía está capacitado para realizar.
// Es independiente de GuiaDestino: la compatibilidad guía + destino + actividad se valida al reservar.
@Entity
@Table(name = "GuiaActividad",
        uniqueConstraints = @UniqueConstraint(name = "UQ_GuiaActividad",
                columnNames = {"IdGuia", "IdActividad"}))
@Getter
@Setter
@NoArgsConstructor
public class GuiaActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdGuiaActividad")
    private Integer idGuiaActividad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdGuia", nullable = false)
    private Guia guia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdActividad", nullable = false)
    private Actividad actividad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;
}
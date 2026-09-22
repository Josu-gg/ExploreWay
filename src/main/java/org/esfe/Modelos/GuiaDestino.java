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

// Destinos en los que trabaja un guía.
// Es independiente de GuiaActividad: la compatibilidad guía + destino + actividad se valida al reservar.
@Entity
@Table(name = "GuiaDestino",
        uniqueConstraints = @UniqueConstraint(name = "UQ_GuiaDestino",
                columnNames = {"IdGuia", "IdDestino"}))
@Getter
@Setter
@NoArgsConstructor
public class GuiaDestino {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "IdGuiaDestino")
    private Integer idGuiaDestino;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name= "IdGuia",  nullable = false)
    private Guia guia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdDestino", nullable = false)
    private Destino destino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;

}

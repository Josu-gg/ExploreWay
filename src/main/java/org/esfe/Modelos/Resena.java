package org.esfe.Modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Resena")
@Getter
@Setter
@NoArgsConstructor
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdResena")
    private Integer idResena;

    // Relación 1:1 (UQ_Resena_Reserva en la BD).
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdReserva", nullable = false, unique = true)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdCliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdGuia", nullable = false)
    private Guia guia;

    // CHK_Resena_Calificacion: entre 1 y 5
    @Column(name = "Calificacion", nullable = false)
    private Integer calificacion;

    @Column(name = "Comentario", length = 1000)
    private String comentario;

    @Column(name = "Fecha", nullable = false, updatable = false)
    private LocalDateTime fecha;
}
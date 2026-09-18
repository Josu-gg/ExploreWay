package org.esfe.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Actividad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdActividad")
    private Integer id;

    @NotBlank(message = "El nombre de la actividad es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Column(name = "Nombre", length = 150, nullable = false, unique = true)
    private String nombre;

    @Column(name = "Descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Size(max = 50, message = "La dificultad no puede superar los 50 caracteres")
    @Column(name = "Dificultad", length = 50)
    private String dificultad;

    @NotNull(message = "El estado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;
}

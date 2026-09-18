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
@Table(name = "Destino")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Destino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDestino")
    private Integer id;

    @NotBlank(message = "El nombre del destino es obligatorio")
    @Size(max = 150, message ="El nombre no puede superar los 150 caracteres")
    @Column(name = "Nombre", length = 150, nullable = false, unique = true)
    private String nombre;

    @Column(name = "Descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank(message = "El departamento es obligatorio")
    @Size(max = 100, message = "El departamento no puede superar los 100 caracteres")
    @Column(name = "Departamento", length = 100, nullable = false)
    private String departamento;

    @Size(max = 100, message = "El municipio no puede superar los 100 caracteres")
    @Column(name = "Municipio", length = 100)
    private String municipio;

    @NotNull(message = "El estado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;
}

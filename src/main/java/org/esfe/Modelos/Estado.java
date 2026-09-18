package org.esfe.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Estado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdEstado")
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre del estado no puede superar los 10 caracteres")
    @Column(name = "NombreEstado", length =50, nullable = false)
    private String nombreEstado;

    @NotBlank(message = "El tipo de estado es obligatorio")
    @Size(max =50, message = "El tipo de estado no puede superar los 50 caracteres")
    @Column(name = "TipoEstado", length = 50, nullable = false)
    private String tipoEstado;


}

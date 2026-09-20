package org.esfe.Modelos;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name = "Persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdPersona")
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max= 100, message = "El nombre no puede sueperar los 100 caracteres")
    @Column(name = "Nombre", length = 100, nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    @Column(name = "Apellido", length = 100, nullable = false)
    private String apellido;

    @NotBlank(message =  "El teléfono es obligatorio")
    @Size(max = 20, message = "El telefono no puede superar los 20 caracteres")
    @Column(name = "Telefono", length = 20, nullable = false)
    private String telefono;

    @Size(max = 250, message = "La direccion no debe superar los 250n caracteres")
    @Column(name = "Direccion", length = 250)
    private String direccion;

    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    @Column(name = "FechaNacimiento")
    private LocalDate fechaNacimiento;

    @Size(max = 500, message = "La foto no debe superar los 500 caracteres")
    @Column(name = "Foto", length = 500)
    private String foto;
}

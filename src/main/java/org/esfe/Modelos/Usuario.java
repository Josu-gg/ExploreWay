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

// Se usan @Getter/@Setter (no @Data) para evitar toString/equals que carguen relaciones
// LAZY o impriman el hash de la contraseña en logs.
@Entity
@Table(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdUsuario")
    private Integer idUsuario;

    // La unicidad (UQ_Usuario_Correo) la garantiza la base de datos.
    @Column(name = "Correo", nullable = false, length = 150)
    private String correo;

    // Hash BCrypt, nunca texto plano.
    @Column(name = "Contra", nullable = false, length = 255)
    private String contra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdRol", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;

    // Relación 1:1 (UQ_Usuario_Persona en la BD).
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdPersona", nullable = false)
    private Persona persona;
}
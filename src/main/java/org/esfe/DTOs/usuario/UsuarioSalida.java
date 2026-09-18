package org.esfe.DTOs.usuario;

import lombok.Builder;
import lombok.Getter;
import org.esfe.Modelos.Usuario;

import java.time.LocalDate;

// Respuesta pública del usuario. Nunca incluye la contraseña ni su hash.
@Getter
@Builder
public class UsuarioSalida {

    private Integer idUsuario;
    private String correo;

    private Integer idRol;
    private String nombreRol;

    private Integer idEstado;
    private String nombreEstado;

    private Integer idPersona;
    private String nombre;
    private String apellido;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private String foto;

    // Debe llamarse dentro de una transacción (las relaciones son LAZY).
    public static UsuarioSalida desde(Usuario u) {
        return UsuarioSalida.builder()
                .idUsuario(u.getIdUsuario())
                .correo(u.getCorreo())
                .idRol(u.getRol().getIdRol())
                .nombreRol(u.getRol().getNombreRol())
                .idEstado(u.getEstado().getIdEstado())
                .nombreEstado(u.getEstado().getNombreEstado())
                .idPersona(u.getPersona().getIdPersona())
                .nombre(u.getPersona().getNombre())
                .apellido(u.getPersona().getApellido())
                .telefono(u.getPersona().getTelefono())
                .direccion(u.getPersona().getDireccion())
                .fechaNacimiento(u.getPersona().getFechaNacimiento())
                .foto(u.getPersona().getFoto())
                .build();
    }
}
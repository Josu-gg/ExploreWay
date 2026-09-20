package org.esfe.DTOs.usuario;

import lombok.Getter;
import org.esfe.DTOs.persona.PersonaSalida;
import org.esfe.Modelos.Usuario;

// Respuesta pública del usuario. Nunca incluye la contraseña ni su hash.
@Getter
public class UsuarioSalida extends PersonaSalida {

    private final Integer idUsuario;
    private final String correo;
    private final Integer idRol;
    private final String nombreRol;
    private final Integer idEstado;
    private final String nombreEstado;

    private UsuarioSalida(Usuario u) {
        super(u.getPersona());
        this.idUsuario = u.getIdUsuario();
        this.correo = u.getCorreo();
        this.idRol = u.getRol().getId();
        this.nombreRol = u.getRol().getNombreRol();
        this.idEstado = u.getEstado().getId();
        this.nombreEstado = u.getEstado().getNombreEstado();
    }

    // Debe llamarse dentro de una transacción (las relaciones son LAZY).
    public static UsuarioSalida desde(Usuario u) {
        return new UsuarioSalida(u);
    }
}

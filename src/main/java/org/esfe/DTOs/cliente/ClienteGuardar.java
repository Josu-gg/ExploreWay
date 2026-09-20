package org.esfe.DTOs.cliente;
import org.esfe.DTOs.usuario.UsuarioRegistroDatos;

// Registro público de cliente. Hereda Persona + correo + contraseña y NO tiene idRol:
// el rol Cliente y el estado inicial los fija el servidor (evita que alguien se registre
// como Administrador enviando un idRol).
public class ClienteGuardar extends UsuarioRegistroDatos {
}

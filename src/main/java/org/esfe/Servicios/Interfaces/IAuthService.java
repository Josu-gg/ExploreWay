package org.esfe.Servicios.Interfaces;
import org.esfe.DTOs.auth.LoginGuardar;
import org.esfe.DTOs.auth.TokenSalida;
import org.esfe.DTOs.usuario.UsuarioSalida;

public interface IAuthService {

    TokenSalida login(LoginGuardar dto);

    UsuarioSalida usuarioActual(String correo);
}

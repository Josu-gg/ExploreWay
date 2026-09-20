package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.usuario.UsuarioGuardar;
import org.esfe.DTOs.usuario.UsuarioModificar;
import org.esfe.DTOs.usuario.UsuarioRegistroDatos;
import org.esfe.DTOs.usuario.UsuarioSalida;
import org.esfe.Modelos.Rol;
import org.esfe.Modelos.Usuario;
import org.springframework.data.domain.Page;

public interface IUsuarioService {

    UsuarioSalida crear(UsuarioGuardar dto);

    UsuarioSalida modificar(Integer id, UsuarioModificar dto);

    UsuarioSalida obtenerPorId(Integer id);

    Page<UsuarioSalida> listar(int pagina, int tamano);

    // Uso interno entre servicios (p. ej. registro de cliente o de guía):
    // crea Persona + Usuario con el rol indicado. No se expone por HTTP.
    Usuario crearConRol(UsuarioRegistroDatos datos, Rol rol);
}

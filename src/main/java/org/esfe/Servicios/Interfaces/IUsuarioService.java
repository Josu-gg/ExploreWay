package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.usuario.UsuarioGuardar;
import org.esfe.DTOs.usuario.UsuarioModificar;
import org.esfe.DTOs.usuario.UsuarioSalida;
import org.springframework.data.domain.Page;

public interface IUsuarioService {

    UsuarioSalida crear(UsuarioGuardar dto);

    UsuarioSalida modificar(Integer id, UsuarioModificar dto);

    UsuarioSalida obtenerPorId(Integer id);

    Page<UsuarioSalida> listar(int pagina, int tamano);
}
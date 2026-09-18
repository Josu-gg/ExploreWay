package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.rol.RolGuardar;
import org.esfe.DTOs.rol.RolModificar;
import org.esfe.DTOs.rol.RolSalida;

import java.util.List;
import java.util.Optional;

public interface IRolService {
    List<RolSalida> listar();
    Optional<RolSalida> buscarPorId(Integer id);
    RolSalida guardar(RolGuardar dto);
    Optional<RolSalida> modificar(Integer id, RolModificar dto);
    boolean eliminar(Integer id);
}

package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.guia.GuiaGuardar;
import org.esfe.DTOs.guia.GuiaModificar;
import  org.esfe.DTOs.guia.GuiaSalida;

import java.util.List;
import java.util.Optional;

public interface IGuiaService {
    List<GuiaSalida> listar();
    List<GuiaSalida> listarDisponibles();
    Optional<GuiaSalida> buscarPorId(Integer id);
    Optional<GuiaSalida> buscarPorPersona(Integer idPersona);
    GuiaSalida guardar(GuiaGuardar dto);
    // Crea Persona + Usuario (rol Guia) + Guia en una sola transacción.
    GuiaSalida registrar(org.esfe.DTOs.guia.GuiaRegistroGuardar dto);
    Optional<GuiaSalida> modificar(Integer id, GuiaModificar dto);
    boolean eliminar(Integer id);
}

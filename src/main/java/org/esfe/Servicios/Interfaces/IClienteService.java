package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.cliente.ClienteGuardar;
import org.esfe.DTOs.cliente.ClienteModificar;
import org.esfe.DTOs.cliente.ClienteSalida;
import org.springframework.data.domain.Page;

public interface IClienteService {

    ClienteSalida registrar(ClienteGuardar dto);

    ClienteSalida obtenerPorId(Integer id);

    Page<ClienteSalida> listar(int pagina, int tamano);

    ClienteSalida modificar(Integer id, ClienteModificar dto);
}
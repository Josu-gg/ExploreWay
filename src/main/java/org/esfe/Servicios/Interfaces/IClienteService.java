package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.cliente.ClienteSalida;
import org.esfe.DTOs.cliente.RegistroCliente;
import org.springframework.data.domain.Page;

public interface IClienteService {

    // Crea Persona + Usuario (rol Cliente) + Cliente en una sola transacción.
    ClienteSalida registrar(RegistroCliente datos);

    ClienteSalida obtenerPorId(Integer id);

    Page<ClienteSalida> listar(int pagina, int tamano);
}
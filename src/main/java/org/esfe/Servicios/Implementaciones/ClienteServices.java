package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.cliente.ClienteSalida;
import org.esfe.DTOs.cliente.RegistroCliente;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Modelos.Cliente;
import org.esfe.Modelos.Usuario;
import org.esfe.Repositorios.IClienteRepository;
import org.esfe.Servicios.Interfaces.IClienteService;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Servicios.Interfaces.IUsuarioService;
import org.esfe.Utilidades.Paginacion;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteServices implements IClienteService {

    private static final String ROL_CLIENTE = "Cliente";
    // Cliente.IdEstado usa el estado general (no hay estados propios de Cliente en la tabla Estado).
    private static final String ESTADO_ACTIVO = "Activo";
    private static final String TIPO_ESTADO_GENERAL = "General";

    private final IClienteRepository clienteRepository;
    private final IUsuarioService usuarioService;
    private final IEstadoService estadoService;

    @Override
    @Transactional
    public ClienteSalida registrar(RegistroCliente datos) {
        // Si algo falla al crear el Cliente, también se revierten Persona y Usuario.
        Usuario usuario = usuarioService.crearCuenta(datos, datos.getContra(), ROL_CLIENTE);

        Cliente cliente = new Cliente();
        cliente.setPersona(usuario.getPersona());
        cliente.setEstado(estadoService.obtenerPorNombreYTipo(ESTADO_ACTIVO, TIPO_ESTADO_GENERAL));

        return ClienteSalida.desde(clienteRepository.save(cliente));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteSalida obtenerPorId(Integer id) {
        return clienteRepository.findById(id)
                .map(ClienteSalida::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteSalida> listar(int pagina, int tamano) {
        return clienteRepository
                .findAll(Paginacion.de(pagina, tamano, "idCliente"))
                .map(ClienteSalida::desde);
    }
}
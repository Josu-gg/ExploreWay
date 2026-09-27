package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.cliente.ClienteGuardar;
import org.esfe.DTOs.cliente.ClienteModificar;
import org.esfe.DTOs.cliente.ClienteSalida;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Modelos.Cliente;
import org.esfe.Modelos.Rol;
import org.esfe.Modelos.Usuario;
import org.esfe.Repositorios.IClienteRepository;
import org.esfe.Repositorios.IRolRepository;
import org.esfe.Servicios.Interfaces.IClienteService;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Servicios.Interfaces.IPersonaService;
import org.esfe.Servicios.Interfaces.IUsuarioService;
import org.esfe.Utilidades.Paginacion;
import org.esfe.Utilidades.UsuarioActual;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteServices implements IClienteService {

    private static final String ROL_CLIENTE = "Cliente";

    private final IClienteRepository clienteRepository;
    private final IRolRepository rolRepository;
    private final IUsuarioService usuarioService;
    private final IPersonaService personaService;
    private final IEstadoService estadoService;
    private final UsuarioActual usuarioActual;

    // Persona y Usuario se crean primero; Cliente cuelga de la misma Persona (no de Usuario).
    // Si algo falla al crear el Cliente, también se revierten Persona y Usuario.
    @Override
    @Transactional
    public ClienteSalida registrar(ClienteGuardar dto) {
        Rol rolCliente = rolRepository.findByNombreRol(ROL_CLIENTE)
                .orElseThrow(() -> new IllegalStateException(
                        "Falta el rol Cliente en la tabla Rol."));

        Usuario usuario = usuarioService.crearConRol(dto, rolCliente);

        Cliente cliente = new Cliente();
        cliente.setPersona(usuario.getPersona());
        // Cliente.IdEstado usa el estado general (no hay estados propios de Cliente en la tabla Estado).
        cliente.setEstado(estadoService.obtenerActivo(IEstadoService.TIPO_GENERAL));

        return ClienteSalida.desde(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClienteSalida modificar(Integer id, ClienteModificar dto) {
        exigirAdminOPropietario(id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado."));

        // Activar/desactivar es decisión del Administrador, no del propio cliente.
        if (!usuarioActual.esAdmin() && !cliente.getEstado().getId().equals(dto.getIdEstado())) {
            throw new AccessDeniedException("Solo el Administrador puede cambiar el estado de un cliente.");
        }

        cliente.setEstado(estadoService.obtenerDeTipo(dto.getIdEstado(), IEstadoService.TIPO_GENERAL));
        personaService.actualizar(cliente.getPersona(), dto);

        return ClienteSalida.desde(clienteRepository.save(cliente));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteSalida obtenerPorId(Integer id) {
        exigirAdminOPropietario(id);
        return clienteRepository.findById(id)
                .map(ClienteSalida::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteSalida> listar(int pagina, int tamano) {
        usuarioActual.exigirAdmin();
        return clienteRepository
                .findAll(Paginacion.de(pagina, tamano, "idCliente"))
                .map(ClienteSalida::desde);
    }

    private void exigirAdminOPropietario(Integer idCliente) {
        if (usuarioActual.esAdmin()) {
            return;
        }
        if (!usuarioActual.esCliente() || !usuarioActual.idCliente().equals(idCliente)) {
            throw new AccessDeniedException("Solo puedes acceder a tus propios datos.");
        }
    }
}

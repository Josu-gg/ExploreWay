package org.esfe.Servicios.Implementaciones;

import org.esfe.DTOs.cliente.ClienteGuardar;
import org.esfe.DTOs.cliente.ClienteModificar;
import org.esfe.DTOs.cliente.ClienteSalida;
import org.esfe.Modelos.Cliente;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Persona;
import org.esfe.Modelos.Rol;
import org.esfe.Modelos.Usuario;
import org.esfe.Repositorios.IClienteRepository;
import org.esfe.Repositorios.IRolRepository;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Servicios.Interfaces.IPersonaService;
import org.esfe.Servicios.Interfaces.IUsuarioService;
import org.esfe.Utilidades.UsuarioActual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServicesTest {

    // Dependencias externas de ClienteServices: todas simuladas.
    @Mock
    private IClienteRepository clienteRepository;

    @Mock
    private IRolRepository rolRepository;

    @Mock
    private IUsuarioService usuarioService;

    @Mock
    private IPersonaService personaService;

    @Mock
    private IEstadoService estadoService;

    @Mock
    private UsuarioActual usuarioActual;

    @InjectMocks
    private ClienteServices clienteServices;

    // CP03 - Registrar nuevo cliente (público)
    @Test
    void deberiaRegistrarClienteExitosamente() {
        // Arrange: datos de prueba
        ClienteGuardar dto = new ClienteGuardar();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setTelefono("7000-0000");
        dto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        dto.setCorreo("juan.perez@exploreway.com");
        dto.setContra("Clave123");

        Rol rolCliente = new Rol(2, "Cliente", null);
        when(rolRepository.findByNombreRol("Cliente")).thenReturn(Optional.of(rolCliente));

        Persona persona = new Persona(1, "Juan", "Pérez", "7000-0000", null,
                LocalDate.of(2000, 1, 1), null, null);
        Usuario usuarioCreado = new Usuario();
        usuarioCreado.setPersona(persona);
        when(usuarioService.crearConRol(dto, rolCliente)).thenReturn(usuarioCreado);

        Estado estadoActivo = new Estado(1, "Activo", "General");
        when(estadoService.obtenerActivo(IEstadoService.TIPO_GENERAL)).thenReturn(estadoActivo);

        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocacion -> {
            Cliente c = invocacion.getArgument(0);
            c.setIdCliente(10);
            return c;
        });

        // Act
        ClienteSalida resultado = clienteServices.registrar(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(10, resultado.getIdCliente());
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Pérez", resultado.getApellido());
        assertEquals(1, resultado.getIdEstado());
        assertEquals("Activo", resultado.getNombreEstado());
    }

    // CP07 - Actualización de perfil de cliente
    @Test
    void deberiaActualizarPerfilDeClienteExitosamente() {
        // Arrange: datos de prueba (equivale a los "Datos de pruebas" del caso CP07)
        Integer idCliente = 5;

        ClienteModificar dto = new ClienteModificar();
        dto.setNombre("Juan");
        dto.setApellido("Pérez Actualizado");
        dto.setTelefono("7000-1111");
        dto.setDireccion("San Salvador");
        dto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        dto.setIdEstado(1);

        // Se simula que quien modifica es Administrador, para no bloquear por la
        // validación de "solo el dueño puede modificar sus propios datos".
        when(usuarioActual.esAdmin()).thenReturn(true);

        Persona personaExistente = new Persona(1, "Juan", "Pérez", "7000-0000", null,
                LocalDate.of(2000, 1, 1), null, null);
        Estado estadoActivo = new Estado(1, "Activo", "General");
        Cliente clienteExistente = new Cliente();
        clienteExistente.setIdCliente(idCliente);
        clienteExistente.setPersona(personaExistente);
        clienteExistente.setEstado(estadoActivo);

        when(clienteRepository.findById(idCliente)).thenReturn(Optional.of(clienteExistente));
        when(estadoService.obtenerDeTipo(dto.getIdEstado(), IEstadoService.TIPO_GENERAL))
                .thenReturn(estadoActivo);

        // Simula lo que hace PersonaServices.actualizar: muta la Persona real con los
        // datos nuevos del dto y la devuelve (igual que haría el dirty checking de Hibernate).
        when(personaService.actualizar(any(Persona.class), eq(dto))).thenAnswer(invocacion -> {
            Persona p = invocacion.getArgument(0);
            p.setNombre(dto.getNombre());
            p.setApellido(dto.getApellido());
            p.setTelefono(dto.getTelefono());
            p.setDireccion(dto.getDireccion());
            p.setFechaNacimiento(dto.getFechaNacimiento());
            return p;
        });

        when(clienteRepository.save(clienteExistente)).thenReturn(clienteExistente);

        // Act: se ejecuta el método real que se está probando
        ClienteSalida resultado = clienteServices.modificar(idCliente, dto);

        // Assert: equivale al "Resultado Esperado" del caso CP07
        assertNotNull(resultado);
        assertEquals(idCliente, resultado.getIdCliente());
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Pérez Actualizado", resultado.getApellido());
        assertEquals("7000-1111", resultado.getTelefono());
        assertEquals("San Salvador", resultado.getDireccion());
        assertEquals(1, resultado.getIdEstado());
        assertEquals("Activo", resultado.getNombreEstado());
    }

    // CP08 - Consultar datos de cliente
    @Test
    void deberiaConsultarClientePorIdExitosamente() {
        // Arrange: datos de prueba (equivale a los "Datos de pruebas" del caso CP08)
        Integer idCliente = 5;

        // Se simula que quien consulta es Administrador, para no bloquear por la
        // validación de "solo puedes consultar tus propios datos".
        when(usuarioActual.esAdmin()).thenReturn(true);

        Persona persona = new Persona(1, "Juan", "Pérez", "7000-0000", "San Salvador",
                LocalDate.of(2000, 1, 1), null, null);
        Estado estadoActivo = new Estado(1, "Activo", "General");
        Cliente cliente = new Cliente();
        cliente.setIdCliente(idCliente);
        cliente.setPersona(persona);
        cliente.setEstado(estadoActivo);

        when(clienteRepository.findById(idCliente)).thenReturn(Optional.of(cliente));

        // Act: se ejecuta el método real que se está probando
        ClienteSalida resultado = clienteServices.obtenerPorId(idCliente);

        // Assert: equivale al "Resultado Esperado" del caso CP08
        assertNotNull(resultado);
        assertEquals(idCliente, resultado.getIdCliente());
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Pérez", resultado.getApellido());
        assertEquals("San Salvador", resultado.getDireccion());
        assertEquals(1, resultado.getIdEstado());
        assertEquals("Activo", resultado.getNombreEstado());
    }
}
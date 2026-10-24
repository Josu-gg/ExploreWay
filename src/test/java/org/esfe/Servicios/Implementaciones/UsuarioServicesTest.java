package org.esfe.Servicios.Implementaciones;

import org.esfe.DTOs.usuario.UsuarioGuardar;
import org.esfe.DTOs.usuario.UsuarioSalida;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Persona;
import org.esfe.Modelos.Rol;
import org.esfe.Modelos.Usuario;
import org.esfe.Repositorios.IRolRepository;
import org.esfe.Repositorios.IUsuarioRepository;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Servicios.Interfaces.IPersonaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServicesTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private IRolRepository rolRepository;

    @Mock
    private IPersonaService personaService;

    @Mock
    private IEstadoService estadoService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServices usuarioServices;

    // CP09 - Registro de usuario interno por administrador
    @Test
    void deberiaCrearUsuarioInternoExitosamente() {
        // ── Datos de prueba ──────────────────────────────────
        UsuarioGuardar dto = new UsuarioGuardar();
        dto.setNombre("Carlos");
        dto.setApellido("Mejía");
        dto.setTelefono("7777-8888");
        dto.setDireccion("Sonsonate, El Salvador");
        dto.setFechaNacimiento(LocalDate.of(1995, 5, 20));
        dto.setFoto("https://ejemplo.com/foto.jpg");
        dto.setCorreo("  Carlos.Guia@ExploreWay.com ");
        dto.setContra("Clave1234");
        dto.setIdRol(3);

        Rol rolGuia = new Rol();
        rolGuia.setId(3);
        rolGuia.setNombreRol("Guia");

        Persona personaCreada = new Persona();
        personaCreada.setId(10);
        personaCreada.setNombre("Carlos");
        personaCreada.setApellido("Mejía");
        personaCreada.setTelefono("7777-8888");
        personaCreada.setDireccion("Sonsonate, El Salvador");
        personaCreada.setFechaNacimiento(LocalDate.of(1995, 5, 20));
        personaCreada.setFoto("https://ejemplo.com/foto.jpg");

        Estado estadoActivo = new Estado();
        estadoActivo.setId(3);
        estadoActivo.setNombreEstado("Activo");
        estadoActivo.setTipoEstado("Usuario");

        String hash = "$2a$10$hashSimuladoDeBCrypt";

        // ── Mocks ────────────────────────────────────────────
        when(rolRepository.findById(3)).thenReturn(Optional.of(rolGuia));
        when(usuarioRepository.existsByCorreo("carlos.guia@exploreway.com")).thenReturn(false);
        when(personaService.crear(dto)).thenReturn(personaCreada);
        when(passwordEncoder.encode("Clave1234")).thenReturn(hash);
        when(estadoService.obtenerActivo(IEstadoService.TIPO_GENERAL)).thenReturn(estadoActivo);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario u = invocacion.getArgument(0);
            u.setIdUsuario(100);
            return u;
        });

        // ── Ejecución ────────────────────────────────────────
        UsuarioSalida resultado = usuarioServices.crear(dto);

        // ── Verificaciones de la respuesta ───────────────────
        assertNotNull(resultado);
        assertEquals(100, resultado.getIdUsuario());
        assertEquals("carlos.guia@exploreway.com", resultado.getCorreo());
        assertEquals(3, resultado.getIdRol());
        assertEquals("Guia", resultado.getNombreRol());
        assertEquals(3, resultado.getIdEstado());
        assertEquals("Activo", resultado.getNombreEstado());
        assertEquals(10, resultado.getIdPersona());
        assertEquals("Carlos", resultado.getNombre());
        assertEquals("Mejía", resultado.getApellido());
        assertEquals("7777-8888", resultado.getTelefono());
        assertEquals("Sonsonate, El Salvador", resultado.getDireccion());
        assertEquals(LocalDate.of(1995, 5, 20), resultado.getFechaNacimiento());
        assertEquals("https://ejemplo.com/foto.jpg", resultado.getFoto());

        // ── Verificaciones de lo que se guardó ───────────────
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();

        assertEquals("carlos.guia@exploreway.com", guardado.getCorreo());
        assertEquals(hash, guardado.getContra());
        assertNotEquals("Clave1234", guardado.getContra());
        assertEquals(rolGuia, guardado.getRol());
        assertEquals(estadoActivo, guardado.getEstado());
        assertEquals(personaCreada, guardado.getPersona());

        verify(passwordEncoder).encode("Clave1234");
        verify(personaService).crear(dto);
    }
}
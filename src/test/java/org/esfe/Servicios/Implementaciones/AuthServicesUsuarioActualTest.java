package org.esfe.Servicios.Implementaciones;

import org.esfe.Config.JwtPropiedades;
import org.esfe.DTOs.usuario.UsuarioSalida;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Persona;
import org.esfe.Modelos.Rol;
import org.esfe.Modelos.Usuario;
import org.esfe.Repositorios.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// CP03 - Consultar datos del perfil en sesión (/api/auth/me)
@ExtendWith(MockitoExtension.class)
class AuthServicesUsuarioActualTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private IUsuarioRepository usuarioRepository;

    private AuthServices authServices;

    @BeforeEach
    void setUp() {
        JwtPropiedades jwtPropiedades = new JwtPropiedades(
                "clave-secreta-de-prueba-min-32-caracteres", 60L);
        authServices = new AuthServices(authenticationManager, jwtEncoder, jwtPropiedades, usuarioRepository);
    }

    @Test
    void deberiaRetornarUsuarioAutenticado() {
        // Arrange: se arma el usuario que "existe" según el token (equivale al Header Authorization del CP03)
        Estado estado = new Estado(1, "Activo", "General");
        Rol rol = new Rol(1, "Admin", estado);

        Persona persona = new Persona();
        persona.setId(1);
        persona.setNombre("Admin");
        persona.setApellido("Sistema");
        persona.setTelefono("0000-0000");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setCorreo("admin@exploreway.com");
        usuario.setRol(rol);
        usuario.setEstado(estado);
        usuario.setPersona(persona);

        when(usuarioRepository.findByCorreo("admin@exploreway.com")).thenReturn(Optional.of(usuario));

        // Act
        UsuarioSalida resultado = authServices.usuarioActual("admin@exploreway.com");

        // Assert: equivale al "Resultado Esperado" del CP03
        assertNotNull(resultado);
        assertEquals("admin@exploreway.com", resultado.getCorreo());
        assertEquals("Admin", resultado.getNombreRol());
        assertEquals("Activo", resultado.getNombreEstado());
    }
}
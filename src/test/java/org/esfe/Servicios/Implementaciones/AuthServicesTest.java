package org.esfe.Servicios.Implementaciones;

import org.esfe.Config.JwtPropiedades;
import org.esfe.DTOs.auth.LoginGuardar;
import org.esfe.DTOs.auth.TokenSalida;
import org.esfe.Repositorios.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// CP01 - Iniciar sesión exitoso de usuario (HU-01)
@ExtendWith(MockitoExtension.class)
class AuthServicesTest {

    // Dependencias externas de AuthServices: se simulan, no se conecta a nada real.
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private IUsuarioRepository usuarioRepository;

    private AuthServices authServices;

    @BeforeEach
    void setUp() {
        // JwtPropiedades es un record simple: se usa un valor real de prueba, no se mockea.
        JwtPropiedades jwtPropiedades = new JwtPropiedades(
                "clave-secreta-de-prueba-min-32-caracteres", 60L);
        authServices = new AuthServices(authenticationManager, jwtEncoder, jwtPropiedades, usuarioRepository);
    }

    @Test
    void deberiaIniciarSesionExitosamente() {
        // Arrange: datos de prueba (equivale a los "Datos de pruebas" del caso CP01)
        LoginGuardar dto = new LoginGuardar();
        dto.setCorreo("admin@exploreway.com");
        dto.setContra("Clave123");

        // Simula lo que haría Spring Security si el correo y contraseña fueran correctos
        Authentication autenticacionSimulada = new TestingAuthenticationToken(
                "admin@exploreway.com", null, List.of(new SimpleGrantedAuthority("ROLE_Admin")));
        when(authenticationManager.authenticate(any())).thenReturn(autenticacionSimulada);

        // Simula la generación del JWT, sin firmar un token real
        Jwt jwtSimulado = Jwt.withTokenValue("token-falso-123")
                .header("alg", "HS256")
                .claim("sub", "admin@exploreway.com")
                .build();
        when(jwtEncoder.encode(any())).thenReturn(jwtSimulado);

        // Act: se ejecuta el método real que se está probando
        TokenSalida resultado = authServices.login(dto);

        // Assert: equivale al "Resultado Esperado" del caso CP01
        assertNotNull(resultado);
        assertEquals("token-falso-123", resultado.getToken());
        assertEquals("Bearer", resultado.getTipoToken());
        assertEquals("admin@exploreway.com", resultado.getCorreo());
        assertEquals("Admin", resultado.getRol());
        assertNotNull(resultado.getExpiraEn());
    }
}
package org.esfe.Servicios.Implementaciones;
import lombok.RequiredArgsConstructor;
import org.esfe.Config.JwtConfig;
import org.esfe.Config.JwtPropiedades;
import org.esfe.DTOs.auth.LoginGuardar;
import org.esfe.DTOs.auth.TokenSalida;
import org.esfe.DTOs.usuario.UsuarioSalida;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Repositorios.IUsuarioRepository;
import org.esfe.Servicios.Interfaces.IAuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthServices implements IAuthService {
    private static final String TIPO_TOKEN = "Bearer";
    private static final String PREFIJO_ROL = "ROLE_";
    private static final String EMISOR = "exploreway";

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtPropiedades jwtPropiedades;
    private final IUsuarioRepository usuarioRepository;

    // Si el correo o la contraseña fallan, authenticate() lanza AuthenticationException (401).
    @Override
    public TokenSalida login(LoginGuardar dto) {
        Authentication autenticacion = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(dto.getCorreo(), dto.getContra()));

        String correo = autenticacion.getName();
        String rol = extraerRol(autenticacion);
        Instant expira = Instant.now().plus(jwtPropiedades.expiracionMinutos(), ChronoUnit.MINUTES);

        return new TokenSalida(generarToken(correo, rol, expira), TIPO_TOKEN, expira, correo, rol);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioSalida usuarioActual(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .map(UsuarioSalida::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
    }

    private String generarToken(String correo, String rol, Instant expira) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(EMISOR)
                .issuedAt(Instant.now())
                .expiresAt(expira)
                .subject(correo)
                .claim(JwtConfig.CLAIM_ROL, rol)
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
    }

    private static String extraerRol(Authentication autenticacion) {
        return autenticacion.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith(PREFIJO_ROL))
                .map(a -> a.substring(PREFIJO_ROL.length()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("El usuario no tiene un rol asignado."));
    }
}

package org.esfe.Servicios.Implementaciones;
import lombok.RequiredArgsConstructor;
import org.esfe.Modelos.Usuario;
import org.esfe.Repositorios.IUsuarioRepository;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioDetallesServices  implements UserDetailsService{
    private final IUsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas."));

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContra())
                .roles(usuario.getRol().getNombreRol())
                .disabled(!IEstadoService.ESTADO_ACTIVO.equals(usuario.getEstado().getNombreEstado()))
                .build();
    }
}

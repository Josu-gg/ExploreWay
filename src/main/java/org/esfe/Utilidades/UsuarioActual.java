package org.esfe.Utilidades;

import lombok.RequiredArgsConstructor;
import org.esfe.Modelos.Cliente;
import org.esfe.Modelos.Guia;
import org.esfe.Repositorios.IClienteRepository;
import org.esfe.Repositorios.IGuiaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Identifica al usuario del token (correo en el subject, rol en las authorities) para que los
// servicios comprueben que cada Cliente/Guía solo toque sus propios recursos.
@Component
@RequiredArgsConstructor
public class UsuarioActual {

    private static final String ROL_ADMIN = "ROLE_Admin";
    private static final String ROL_CLIENTE = "ROLE_Cliente";
    private static final String ROL_GUIA = "ROLE_Guia";

    private final IClienteRepository clienteRepository;
    private final IGuiaRepository guiaRepository;

    public boolean esAdmin() {
        return tieneRol(ROL_ADMIN);
    }

    public boolean esCliente() {
        return tieneRol(ROL_CLIENTE);
    }

    public boolean esGuia() {
        return tieneRol(ROL_GUIA);
    }

    public void exigirAdmin() {
        if (!esAdmin()) {
            throw new AccessDeniedException("Solo el Administrador puede realizar esta acción.");
        }
    }

    // Id del Cliente autenticado; falla si el usuario no es Cliente.
    public Integer idCliente() {
        return clienteRepository.findByCorreoUsuario(correo())
                .map(Cliente::getIdCliente)
                .orElseThrow(() -> new AccessDeniedException("El usuario autenticado no es un cliente."));
    }

    // Id del Guía autenticado; falla si el usuario no es Guía.
    public Integer idGuia() {
        return guiaRepository.findByCorreoUsuario(correo())
                .map(Guia::getId)
                .orElseThrow(() -> new AccessDeniedException("El usuario autenticado no es un guía."));
    }

    private String correo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("No hay un usuario autenticado.");
        }
        return auth.getName();
    }

    private boolean tieneRol(String rol) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> rol.equals(a.getAuthority()));
    }
}

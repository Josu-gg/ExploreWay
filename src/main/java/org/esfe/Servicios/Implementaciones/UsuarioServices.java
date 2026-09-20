package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.usuario.UsuarioGuardar;
import org.esfe.DTOs.usuario.UsuarioModificar;
import org.esfe.DTOs.usuario.UsuarioRegistroDatos;
import org.esfe.DTOs.usuario.UsuarioSalida;
import org.esfe.Excepciones.ConflictoException;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Modelos.Persona;
import org.esfe.Modelos.Rol;
import org.esfe.Modelos.Usuario;
import org.esfe.Repositorios.IRolRepository;
import org.esfe.Repositorios.IUsuarioRepository;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Servicios.Interfaces.IPersonaService;
import org.esfe.Servicios.Interfaces.IUsuarioService;
import org.esfe.Utilidades.Paginacion;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioServices implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final IRolRepository rolRepository;
    private final IPersonaService personaService;
    private final IEstadoService estadoService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioSalida crear(UsuarioGuardar dto) {
        return UsuarioSalida.desde(crearConRol(dto, buscarRol(dto.getIdRol())));
    }

    // Crea Persona + Usuario. Si algo falla, la transacción del llamador revierte ambos.
    @Override
    @Transactional
    public Usuario crearConRol(UsuarioRegistroDatos datos, Rol rol) {
        String correo = normalizarCorreo(datos.getCorreo());

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ConflictoException("Ya existe un usuario registrado con ese correo.");
        }

        Persona persona = personaService.crear(datos);

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContra(passwordEncoder.encode(datos.getContra()));
        usuario.setRol(rol);
        usuario.setEstado(estadoService.obtenerActivo(IEstadoService.TIPO_USUARIO));
        usuario.setPersona(persona);

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public UsuarioSalida modificar(Integer id, UsuarioModificar dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));

        String correo = normalizarCorreo(dto.getCorreo());
        if (usuarioRepository.existsByCorreoAndIdUsuarioNot(correo, id)) {
            throw new ConflictoException("Ya existe otro usuario registrado con ese correo.");
        }

        usuario.setCorreo(correo);
        usuario.setRol(buscarRol(dto.getIdRol()));
        // No se puede asignar un estado de Reserva, Pago, etc.: debe ser de tipo "Usuario".
        usuario.setEstado(estadoService.obtenerDeTipo(dto.getIdEstado(), IEstadoService.TIPO_USUARIO));
        personaService.actualizar(usuario.getPersona(), dto);

        // saveAndFlush para que un choque de unicidad falle aquí y no al cerrar la transacción.
        return UsuarioSalida.desde(usuarioRepository.saveAndFlush(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioSalida obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .map(UsuarioSalida::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioSalida> listar(int pagina, int tamano) {
        return usuarioRepository
                .findAll(Paginacion.de(pagina, tamano, "idUsuario"))
                .map(UsuarioSalida::desde);
    }

    private Rol buscarRol(Integer idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new RecursoNoEncontradoException("El rol indicado no existe."));
    }

    // El correo se normaliza para que "Ana@x.com" y "ana@x.com" no sean dos cuentas.
    private static String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}

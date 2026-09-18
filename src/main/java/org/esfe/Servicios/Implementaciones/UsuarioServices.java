package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.usuario.UsuarioDatos;
import org.esfe.DTOs.usuario.UsuarioGuardar;
import org.esfe.DTOs.usuario.UsuarioModificar;
import org.esfe.DTOs.usuario.UsuarioSalida;
import org.esfe.Excepciones.ConflictoException;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Persona;
import org.esfe.Modelos.Rol;
import org.esfe.Modelos.Usuario;
import org.esfe.Repositorios.IEstadoRepository;
import org.esfe.Repositorios.IPersonaRepository;
import org.esfe.Repositorios.IRolRepository;
import org.esfe.Repositorios.IUsuarioRepository;
import org.esfe.Servicios.Interfaces.IUsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioServices implements IUsuarioService {

    // Los estados se buscan por nombre + tipo (no por Id fijo) porque en la tabla Estado
    // hay varios "Activo" (General, Usuario, Disponibilidad).
    private static final String ESTADO_ACTIVO = "Activo";
    private static final String TIPO_ESTADO_USUARIO = "Usuario";
    private static final int TAMANO_MAXIMO_PAGINA = 50;

    private final IUsuarioRepository usuarioRepository;
    private final IPersonaRepository personaRepository;
    private final IRolRepository rolRepository;
    private final IEstadoRepository estadoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioSalida crear(UsuarioGuardar dto) {
        String correo = normalizarCorreo(dto.getCorreo());

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ConflictoException("Ya existe un usuario registrado con ese correo.");
        }

        Rol rol = buscarRol(dto.getIdRol());

        Estado estadoActivo = estadoRepository
                .findByNombreEstadoAndTipoEstado(ESTADO_ACTIVO, TIPO_ESTADO_USUARIO)
                .orElseThrow(() -> new IllegalStateException(
                        "Falta el estado 'Activo' de tipo 'Usuario' en la tabla Estado."));

        Persona persona = new Persona();
        aplicarDatosPersona(persona, dto);
        personaRepository.save(persona);

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContra(passwordEncoder.encode(dto.getContra()));
        usuario.setRol(rol);
        usuario.setEstado(estadoActivo);
        usuario.setPersona(persona);

        return UsuarioSalida.desde(usuarioRepository.save(usuario));
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

        Rol rol = buscarRol(dto.getIdRol());

        // El estado debe existir y ser de tipo "Usuario" (no se puede asignar un estado de Reserva, Pago, etc.).
        Estado estado = estadoRepository.findById(dto.getIdEstado())
                .filter(e -> TIPO_ESTADO_USUARIO.equals(e.getTipoEstado()))
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El estado indicado no existe o no corresponde a usuarios."));

        usuario.setCorreo(correo);
        usuario.setRol(rol);
        usuario.setEstado(estado);
        aplicarDatosPersona(usuario.getPersona(), dto);

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
        // El orden es fijo: no se acepta sort desde el cliente para no permitir
        // ordenar por columnas sensibles (p. ej. Contra).
        int paginaSegura = Math.max(pagina, 0);
        int tamanoSeguro = Math.min(Math.max(tamano, 1), TAMANO_MAXIMO_PAGINA);

        return usuarioRepository
                .findAll(PageRequest.of(paginaSegura, tamanoSeguro, Sort.by("idUsuario")))
                .map(UsuarioSalida::desde);
    }

    // ── Métodos auxiliares (compartidos por crear y modificar) ─────────────

    private Rol buscarRol(Integer idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new RecursoNoEncontradoException("El rol indicado no existe."));
    }

    // El correo se normaliza para que "Ana@x.com" y "ana@x.com" no sean dos cuentas.
    private static String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }

    private static void aplicarDatosPersona(Persona persona, UsuarioDatos dto) {
        persona.setNombre(dto.getNombre().trim());
        persona.setApellido(dto.getApellido().trim());
        persona.setTelefono(dto.getTelefono().trim());
        persona.setDireccion(limpiarOpcional(dto.getDireccion()));
        persona.setFechaNacimiento(dto.getFechaNacimiento());
        persona.setFoto(limpiarOpcional(dto.getFoto()));
    }

    private static String limpiarOpcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
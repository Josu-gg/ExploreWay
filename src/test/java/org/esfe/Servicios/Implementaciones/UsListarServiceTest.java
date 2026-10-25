package org.esfe.Servicios.Implementaciones;

import org.esfe.Controladores.UsuarioController;
import org.esfe.DTOs.usuario.UsuarioSalida;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Persona;
import org.esfe.Modelos.Rol;
import org.esfe.Modelos.Usuario;
import org.esfe.Servicios.Interfaces.IUsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// CP-12: Listado de usuarios internos registrados (paginado) vía GET /api/usuarios
@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsListarServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUsuarioService usuarioService;

    @Test
    void cp12_listarUsuarios_retorna200ConPaginaDeUsuarios() throws Exception {
        UsuarioSalida usuario1 = construirUsuarioSalida(1, "Carlos", "Martínez", "carlos@exploreway.com", 2, "Admin");
        UsuarioSalida usuario2 = construirUsuarioSalida(2, "Ana", "López", "ana@exploreway.com", 3, "Guia");

        Page<UsuarioSalida> paginaEsperada = new PageImpl<>(
                List.of(usuario1, usuario2),
                PageRequest.of(0, 20),
                2);

        when(usuarioService.listar(anyInt(), anyInt())).thenReturn(paginaEsperada);

        mockMvc.perform(get("/api/usuarios")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].idUsuario").value(1))
                .andExpect(jsonPath("$.content[0].correo").value("carlos@exploreway.com"))
                .andExpect(jsonPath("$.content[1].idUsuario").value(2))
                .andExpect(jsonPath("$.content[1].nombreRol").value("Guia"))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    private UsuarioSalida construirUsuarioSalida(Integer idUsuario, String nombre, String apellido,
                                                 String correo, Integer idRol, String nombreRol) {
        Persona persona = new Persona();
        persona.setId(idUsuario * 10);
        persona.setNombre(nombre);
        persona.setApellido(apellido);
        persona.setTelefono("7890-1234");

        Estado estadoGeneral = new Estado(1, "Activo", "General");
        Rol rol = new Rol(idRol, nombreRol, estadoGeneral);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setCorreo(correo);
        usuario.setContra("$2a$10$hashSimuladoNoSeExpone");
        usuario.setRol(rol);
        usuario.setEstado(estadoGeneral);
        usuario.setPersona(persona);

        return UsuarioSalida.desde(usuario);
    }

}

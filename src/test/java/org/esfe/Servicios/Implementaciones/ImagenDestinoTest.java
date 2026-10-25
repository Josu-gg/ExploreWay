package org.esfe.Servicios.Implementaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.esfe.Controladores.ImagenDestinoController;
import org.esfe.DTOs.ImagenDestino.ImagenDestinoGuardar;
import org.esfe.DTOs.ImagenDestino.ImagenDestinoSalida;
import org.esfe.Servicios.Interfaces.IIImagenDestinoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

// CP-38: Carga de fotografía a la galería del destino vía POST /api/destinos/{idDestino}/imagenes
@WebMvcTest(ImagenDestinoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ImagenDestinoTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IIImagenDestinoService imagenDestinoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void cp38_agregarImagen_retorna201ConImagenYLocation() throws Exception {
        Integer idDestino = 7;

        ImagenDestinoGuardar dto = new ImagenDestinoGuardar(
                "https://res.cloudinary.com/exploreway/cerro-verde-1.jpg",
                "Vista panorámica desde el mirador",
                true);

        ImagenDestinoSalida respuestaEsperada = new ImagenDestinoSalida(
                12, dto.urlImagen(), dto.descripcion(), true);

        when(imagenDestinoService.agregar(eq(idDestino), any(ImagenDestinoGuardar.class)))
                .thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/destinos/{idDestino}/imagenes", idDestino)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        containsString("/api/destinos/" + idDestino + "/imagenes/12")))
                .andExpect(jsonPath("$.idImagen").value(12))
                .andExpect(jsonPath("$.urlImagen").value(dto.urlImagen()))
                .andExpect(jsonPath("$.descripcion").value(dto.descripcion()))
                .andExpect(jsonPath("$.esPrincipal").value(true));
    }

    //CP-41 Definir una imagen como principal
    @Test
    void cp41_marcarComoPrincipal_retorna200ConImagenMarcada() throws Exception {
        Integer idDestino = 7;
        Integer idImagen = 15;

        ImagenDestinoSalida respuestaEsperada = new ImagenDestinoSalida(
                idImagen,
                "https://res.cloudinary.com/exploreway/cerro-verde-2.jpg",
                "Vista desde el sendero principal",
                true);

        when(imagenDestinoService.marcarComoPrincipal(eq(idDestino), eq(idImagen)))
                .thenReturn(respuestaEsperada);

        mockMvc.perform(patch("/api/destinos/{idDestino}/imagenes/{idImagen}/principal",
                        idDestino, idImagen))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idImagen").value(idImagen))
                .andExpect(jsonPath("$.urlImagen").value(respuestaEsperada.urlImagen()))
                .andExpect(jsonPath("$.esPrincipal").value(true));
    }

        // CP-42: Eliminación de fotografía de galería
        @Test
        void cp42_eliminarImagen_retorna204ImagenEliminada() throws Exception {
            Integer idDestino = 7;
            Integer idImagen = 15;

            doNothing().when(imagenDestinoService).eliminar(idDestino, idImagen);

            mockMvc.perform(delete("/api/destinos/{idDestino}/imagenes/{idImagen}", idDestino, idImagen))
                    .andExpect(status().isNoContent());

            verify(imagenDestinoService).eliminar(idDestino, idImagen);
        }

        // CP-43: Consulta de galería de imágenes por destino
        @Test
        void cp43_listarImagenesPorDestino_retorna200ConPrincipalPrimero() throws Exception {
            Integer idDestino = 7;

            ImagenDestinoSalida principal = new ImagenDestinoSalida(
                    12, "https://res.cloudinary.com/exploreway/cerro-verde-1.jpg",
                    "Vista panorámica desde el mirador", true);
            ImagenDestinoSalida secundaria = new ImagenDestinoSalida(
                    13, "https://res.cloudinary.com/exploreway/cerro-verde-2.jpg",
                    "Vista desde el sendero principal", false);

            when(imagenDestinoService.listarPorDestino(eq(idDestino)))
                    .thenReturn(List.of(principal, secundaria));

            mockMvc.perform(get("/api/destinos/{idDestino}/imagenes", idDestino))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].idImagen").value(12))
                    .andExpect(jsonPath("$[0].esPrincipal").value(true))
                    .andExpect(jsonPath("$[1].idImagen").value(13))
                    .andExpect(jsonPath("$[1].esPrincipal").value(false));
        }
}

package org.esfe.Servicios.Implementaciones;

import org.esfe.DTOs.ImagenDestino.*;
import org.esfe.Excepciones.ConflictoException;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Modelos.Destino;
import org.esfe.Modelos.ImagenDestino;
import org.esfe.Repositorios.IDestinoRepository;
import org.esfe.Repositorios.ImagenDestinoRepository;
import org.esfe.Servicios.Interfaces.IIImagenDestinoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImagenDestinoServices implements IIImagenDestinoService {
    private static final int MAX_IMAGENES_DESTINO = 10;
    private final ImagenDestinoRepository imagenDestinoRepository;
    private final IDestinoRepository destinoRepository;

    @Override
    public List<ImagenDestinoSalida> listarPorDestino(Integer idDestino) {
        validarDestinoExiste(idDestino);
        return imagenDestinoRepository
                .findByDestino_IdOrderByEsPrincipalDescIdImagen(idDestino)
                .stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional
    public ImagenDestinoSalida agregar(Integer idDestino, ImagenDestinoGuardar dto) {
        Destino destino = destinoRepository.findById(idDestino)
                .orElseThrow(() -> new RecursoNoEncontradoException("Destino no encontrado"));

        long total = imagenDestinoRepository.countByDestino_Id(idDestino);
        if (total >= MAX_IMAGENES_DESTINO) {
            throw new ConflictoException(
                    "El destino ya tiene el máximo de " + MAX_IMAGENES_DESTINO + " imágenes");
        }

        String url = dto.urlImagen().trim();
        if (imagenDestinoRepository.existsByDestino_IdAndUrlImagen(idDestino, url)) {
            throw new ConflictoException("Esta imagen ya está registrada para el destino");
        }

        // La primera imagen siempre es la portada
        boolean seraPrincipal = total == 0 || Boolean.TRUE.equals(dto.esPrincipal());
        if (seraPrincipal) {
            desmarcarPrincipalActual(idDestino);
        }

        ImagenDestino imagen = new ImagenDestino();
        imagen.setDestino(destino);
        imagen.setUrlImagen(url);
        imagen.setDescripcion(normalizar(dto.descripcion()));
        imagen.setEsPrincipal(seraPrincipal);

        return aDto(imagenDestinoRepository.save(imagen));
    }

    @Override
    @Transactional
    public ImagenDestinoSalida actualizarDescripcion(Integer idDestino, Integer idImagen,
                                                           ImagenDestinoModificar dto) {
        ImagenDestino imagen = obtenerImagenDelDestino(idDestino, idImagen);
        imagen.setDescripcion(normalizar(dto.descripcion()));
        return aDto(imagen); // dirty checking: Hibernate guarda el cambio al cerrar la transacción
    }

    @Override
    @Transactional
    public ImagenDestinoSalida marcarComoPrincipal(Integer idDestino, Integer idImagen) {
        ImagenDestino imagen = obtenerImagenDelDestino(idDestino, idImagen);
        if (!Boolean.TRUE.equals(imagen.getEsPrincipal())) {
            desmarcarPrincipalActual(idDestino);
            imagen.setEsPrincipal(true);
        }
        return aDto(imagen);
    }

    @Override
    @Transactional
    public void eliminar(Integer idDestino, Integer idImagen) {
        ImagenDestino imagen = obtenerImagenDelDestino(idDestino, idImagen);
        boolean eraPrincipal = Boolean.TRUE.equals(imagen.getEsPrincipal());

        imagenDestinoRepository.delete(imagen);

        // Si se borró la portada, se promueve la imagen más antigua restante
        if (eraPrincipal) {
            imagenDestinoRepository.findFirstByDestino_IdOrderByIdImagenAsc(idDestino)
                    .ifPresent(siguiente -> siguiente.setEsPrincipal(true));
        }
    }

    @Override
    public Map<Integer, String> obtenerPortadas(Collection<Integer> idsDestino) {
        if (idsDestino == null || idsDestino.isEmpty()) {
            return Collections.emptyMap();
        }
        return imagenDestinoRepository.findByDestino_IdInAndEsPrincipalTrue(idsDestino)
                .stream()
                .collect(Collectors.toMap(
                        img -> img.getDestino().getId(),
                        ImagenDestino::getUrlImagen,
                        (primera, segunda) -> primera));
    }

    // ── Métodos privados reutilizables ─────────────────────────────

    private ImagenDestino obtenerImagenDelDestino(Integer idDestino, Integer idImagen) {
        validarDestinoExiste(idDestino);
        return imagenDestinoRepository.findByIdImagenAndDestino_Id(idImagen, idDestino)
                .orElseThrow(() -> new RecursoNoEncontradoException("Imagen no encontrada en este destino"));
    }

    private void validarDestinoExiste(Integer idDestino) {
        if (!destinoRepository.existsById(idDestino)) {
            throw new RecursoNoEncontradoException("Destino no encontrado");
        }
    }

    private void desmarcarPrincipalActual(Integer idDestino) {
        imagenDestinoRepository.findByDestino_IdAndEsPrincipalTrue(idDestino)
                .forEach(img -> img.setEsPrincipal(false));
    }

    private String normalizar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    private ImagenDestinoSalida aDto(ImagenDestino img) {
        return new ImagenDestinoSalida(
                img.getIdImagen(), img.getUrlImagen(), img.getDescripcion(), img.getEsPrincipal());
    }
}

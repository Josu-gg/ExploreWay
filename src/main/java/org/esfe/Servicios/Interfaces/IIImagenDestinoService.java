package org.esfe.Servicios.Interfaces;
import org.esfe.DTOs.ImagenDestino.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
public interface IIImagenDestinoService {
    List<ImagenDestinoSalida> listarPorDestino(Integer idDestino);

    ImagenDestinoSalida agregar(Integer idDestino, ImagenDestinoGuardar dto);

    ImagenDestinoSalida actualizarDescripcion(Integer idDestino, Integer idImagen, ImagenDestinoModificar dto);

    ImagenDestinoSalida marcarComoPrincipal(Integer idDestino, Integer idImagen);

    void eliminar(Integer idDestino, Integer idImagen);

    // Reutilizable por DestinoServicio: idDestino -> URL de portada
    Map<Integer, String> obtenerPortadas(Collection<Integer> idsDestino);
}

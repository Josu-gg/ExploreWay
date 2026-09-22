package org.esfe.Servicios.Interfaces;
import org.esfe.DTOs.comun.CambioEstadoModificar;
import org.esfe.DTOs.guiadestino.GuiaDestinoGuardar;
import org.esfe.DTOs.guiadestino.GuiaDestinoSalida;
import org.springframework.data.domain.Page;

import java.util.List;
public interface IGuiaDestinoService {
    List<GuiaDestinoSalida> listarActivosPorGuia(Integer idGuia);
    GuiaDestinoSalida obtenerPorId(Integer id);

    //Administracion
    Page<GuiaDestinoSalida> listar(int pagina, int tamano);

    GuiaDestinoSalida crear(GuiaDestinoGuardar  dto);

    GuiaDestinoSalida modificar(Integer id,CambioEstadoModificar dto);

    void eliminar(Integer id);
}

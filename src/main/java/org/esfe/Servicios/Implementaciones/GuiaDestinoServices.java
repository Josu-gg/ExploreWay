package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.comun.CambioEstadoModificar;
import org.esfe.DTOs.guiadestino.GuiaDestinoGuardar;
import org.esfe.DTOs.guiadestino.GuiaDestinoSalida;
import org.esfe.Excepciones.ConflictoException;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Modelos.Destino;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.GuiaDestino;
import org.esfe.Repositorios.IDestinoRepository;
import org.esfe.Repositorios.IGuiaDestinoRepository;
import org.esfe.Repositorios.IGuiaRepository;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Servicios.Interfaces.IGuiaDestinoService;
import org.esfe.Utilidades.Paginacion;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuiaDestinoServices implements IGuiaDestinoService {

    private final IGuiaDestinoRepository guiaDestinoRepository;
    private final IGuiaRepository guiaRepository;
    private final IDestinoRepository destinoRepository;
    private final IEstadoService estadoService;

    @Override
    @Transactional(readOnly = true)
    public List<GuiaDestinoSalida> listarActivosPorGuia(Integer idGuia) {
        if (!guiaRepository.existsById(idGuia)) {
            throw new RecursoNoEncontradoException("Guía no encontrado.");
        }
        return guiaDestinoRepository.buscarPorGuiaYEstado(idGuia, IEstadoService.ESTADO_ACTIVO)
                .stream()
                .map(GuiaDestinoSalida::desde)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GuiaDestinoSalida obtenerPorId(Integer id) {
        return GuiaDestinoSalida.desde(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GuiaDestinoSalida> listar(int pagina, int tamano) {
        return guiaDestinoRepository
                .findAll(Paginacion.de(pagina, tamano, "idGuiaDestino"))
                .map(GuiaDestinoSalida::desde);
    }

    @Override
    @Transactional
    public GuiaDestinoSalida crear(GuiaDestinoGuardar dto) {
        Guia guia = guiaRepository.findById(dto.getIdGuia())
                .orElseThrow(() -> new RecursoNoEncontradoException("El guía indicado no existe."));
        Destino destino = destinoRepository.findById(dto.getIdDestino())
                .orElseThrow(() -> new RecursoNoEncontradoException("El destino indicado no existe."));

        if (guiaDestinoRepository.existsByGuia_IdAndDestino_Id(guia.getId(), destino.getId())) {
            throw new ConflictoException("El guía ya tiene asignado ese destino.");
        }

        GuiaDestino guiaDestino = new GuiaDestino();
        guiaDestino.setGuia(guia);
        guiaDestino.setDestino(destino);
        guiaDestino.setEstado(estadoService.obtenerActivo(IEstadoService.TIPO_GENERAL));

        return GuiaDestinoSalida.desde(guiaDestinoRepository.saveAndFlush(guiaDestino));
    }

    @Override
    @Transactional
    public GuiaDestinoSalida modificar(Integer id, CambioEstadoModificar dto) {
        GuiaDestino guiaDestino = buscar(id);
        guiaDestino.setEstado(estadoService.obtenerDeTipo(dto.getIdEstado(), IEstadoService.TIPO_GENERAL));
        return GuiaDestinoSalida.desde(guiaDestinoRepository.save(guiaDestino));
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!guiaDestinoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Asignación de destino no encontrada.");
        }
        guiaDestinoRepository.deleteById(id);
    }

    private GuiaDestino buscar(Integer id) {
        return guiaDestinoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación de destino no encontrada."));
    }
}

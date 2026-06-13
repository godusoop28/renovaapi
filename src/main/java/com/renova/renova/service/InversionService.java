package com.renova.renova.service;

import com.renova.renova.dto.InversionRequest;
import com.renova.renova.dto.InversionResponse;
import com.renova.renova.dto.InversionSummary;
import com.renova.renova.dto.PasoDTO;
import com.renova.renova.exception.ResourceNotFoundException;
import com.renova.renova.model.EstadoPaso;
import com.renova.renova.model.EtapaInversion;
import com.renova.renova.model.EtapasInversion;
import com.renova.renova.model.Inversion;
import com.renova.renova.model.PasoInversion;
import com.renova.renova.repository.InversionRepository;
import com.renova.renova.security.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class InversionService {

    private final InversionRepository inversionRepository;

    public InversionService(InversionRepository inversionRepository) {
        this.inversionRepository = inversionRepository;
    }

    @Transactional(readOnly = true)
    public InversionResponse buscarPorCodigo(String codigo) {
        Inversion inversion = inversionRepository
                .findByCodigoSeguimientoAndActivoTrue(codigo.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró una inversión con ese folio de inversión"));
        return InversionResponse.from(inversion);
    }

    @Transactional(readOnly = true)
    public List<InversionSummary> listarTodas() {
        return inversionRepository.findAll().stream()
                .map(InversionSummary::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public InversionResponse obtener(Long id) {
        return InversionResponse.from(buscarEntidad(id));
    }

    @Transactional
    public InversionResponse crear(InversionRequest request) {
        Inversion inversion = new Inversion();
        aplicarDatos(inversion, request);

        String codigo = (request.codigoSeguimiento() == null || request.codigoSeguimiento().isBlank())
                ? generarCodigoUnico()
                : request.codigoSeguimiento().trim();

        if (inversionRepository.existsByCodigoSeguimiento(codigo)) {
            throw new IllegalArgumentException("Ya existe una inversión con ese folio de inversión");
        }
        inversion.setCodigoSeguimiento(codigo);

        return InversionResponse.from(inversionRepository.save(inversion));
    }

    @Transactional
    public InversionResponse actualizar(Long id, InversionRequest request) {
        Inversion inversion = buscarEntidad(id);

        if (request.codigoSeguimiento() != null && !request.codigoSeguimiento().isBlank()) {
            String nuevoCodigo = request.codigoSeguimiento().trim();
            if (!nuevoCodigo.equals(inversion.getCodigoSeguimiento())
                    && inversionRepository.existsByCodigoSeguimiento(nuevoCodigo)) {
                throw new IllegalArgumentException("Ya existe una inversión con ese folio de inversión");
            }
            inversion.setCodigoSeguimiento(nuevoCodigo);
        }

        aplicarDatos(inversion, request);
        return InversionResponse.from(inversionRepository.save(inversion));
    }

    /** Desactiva la inversion en lugar de borrarla, para no perder el historial. */
    @Transactional
    public void eliminar(Long id) {
        Inversion inversion = buscarEntidad(id);
        inversion.setActivo(false);
        inversionRepository.save(inversion);
    }

    private Inversion buscarEntidad(Long id) {
        return inversionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inversion no encontrada"));
    }

    private void aplicarDatos(Inversion inversion, InversionRequest request) {
        inversion.setNombreInversionista(request.nombreInversionista());
        inversion.setFolio(request.folio());
        inversion.setPropiedad(request.propiedad());
        inversion.setMonto(request.monto());
        inversion.setRendimientoEstimado(request.rendimientoEstimado());
        inversion.setFechaInicio(request.fechaInicio());
        inversion.setFechaCierreEstimada(request.fechaCierreEstimada());
        inversion.setAsesor(request.asesor());
        inversion.setEstado(normalizarEstado(request.estado()));
        inversion.setActivo(request.activo() == null || request.activo());

        inversion.getPasos().clear();
        for (PasoDTO pasoDTO : normalizarPasos(request.pasos())) {
            inversion.getPasos().add(PasoInversion.builder()
                    .numero(pasoDTO.numero())
                    .titulo(pasoDTO.titulo())
                    .descripcion(pasoDTO.descripcion())
                    .fecha(pasoDTO.fecha())
                    .estado(pasoDTO.estado())
                    .inversion(inversion)
                    .build());
        }

        inversion.setImagenesAntes(limpiarImagenes(request.imagenesAntes()));
        inversion.setImagenesDespues(limpiarImagenes(request.imagenesDespues()));
    }

    /**
     * Garantiza que el estado general de la inversion sea una de las 6 fases
     * definitivas. Si no coincide con ninguna, conserva el valor recibido tal
     * cual (por ejemplo, para inversiones existentes con estados anteriores que
     * aun no se han actualizado).
     */
    private String normalizarEstado(String estado) {
        return EtapasInversion.porTitulo(estado)
                .map(EtapaInversion::titulo)
                .orElse(estado);
    }

    /**
     * Reconstruye la lista de pasos a partir de las 6 fases definitivas, en su
     * orden correcto. Para cada fase, si el request incluye un paso cuyo titulo
     * coincide (sin importar mayusculas/acentos de capitalizacion), se conservan
     * su descripcion, fecha y estado; de lo contrario se usa la descripcion por
     * defecto y estado PENDIENTE. Cualquier paso del request con un titulo que no
     * corresponda a una fase definitiva (fases antiguas, duplicados, etc.) se
     * descarta.
     */
    private List<PasoDTO> normalizarPasos(List<PasoDTO> pasos) {
        List<PasoDTO> entrada = pasos == null ? List.of() : pasos;
        List<PasoDTO> resultado = new ArrayList<>(EtapasInversion.ETAPAS.size());

        for (EtapaInversion etapa : EtapasInversion.ETAPAS) {
            PasoDTO existente = entrada.stream()
                    .filter(p -> p.titulo() != null && etapa.titulo().equals(p.titulo().trim().toUpperCase()))
                    .findFirst()
                    .orElse(null);

            if (existente != null) {
                String descripcion = existente.descripcion() == null || existente.descripcion().isBlank()
                        ? etapa.descripcion()
                        : existente.descripcion();
                EstadoPaso estado = existente.estado() == null ? EstadoPaso.PENDIENTE : existente.estado();
                resultado.add(new PasoDTO(existente.id(), etapa.numero(), etapa.titulo(), descripcion, existente.fecha(), estado));
            } else {
                resultado.add(new PasoDTO(null, etapa.numero(), etapa.titulo(), etapa.descripcion(), null, EstadoPaso.PENDIENTE));
            }
        }

        return resultado;
    }

    /** Quita URLs vacias y limita la lista a 4 elementos. */
    private List<String> limpiarImagenes(List<String> urls) {
        if (urls == null) return new ArrayList<>();
        return urls.stream()
                .filter(url -> url != null && !url.isBlank())
                .map(String::trim)
                .limit(4)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            codigo = TokenService.randomCode(9);
        } while (inversionRepository.existsByCodigoSeguimiento(codigo));
        return codigo;
    }
}

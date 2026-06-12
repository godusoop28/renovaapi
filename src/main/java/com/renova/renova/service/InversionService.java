package com.renova.renova.service;

import com.renova.renova.dto.InversionRequest;
import com.renova.renova.dto.InversionResponse;
import com.renova.renova.dto.InversionSummary;
import com.renova.renova.dto.PasoDTO;
import com.renova.renova.exception.ResourceNotFoundException;
import com.renova.renova.model.Inversion;
import com.renova.renova.model.PasoInversion;
import com.renova.renova.repository.InversionRepository;
import com.renova.renova.security.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro una inversion con ese codigo de seguimiento"));
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
            throw new IllegalArgumentException("Ya existe una inversion con ese codigo de seguimiento");
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
                throw new IllegalArgumentException("Ya existe una inversion con ese codigo de seguimiento");
            }
            inversion.setCodigoSeguimiento(nuevoCodigo);
        }

        aplicarDatos(inversion, request);
        return InversionResponse.from(inversionRepository.save(inversion));
    }

    @Transactional
    public void eliminar(Long id) {
        Inversion inversion = buscarEntidad(id);
        inversionRepository.delete(inversion);
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
        inversion.setEstado(request.estado());
        inversion.setActivo(request.activo() == null || request.activo());

        inversion.getPasos().clear();
        if (request.pasos() != null) {
            for (PasoDTO pasoDTO : request.pasos()) {
                inversion.getPasos().add(PasoInversion.builder()
                        .numero(pasoDTO.numero())
                        .titulo(pasoDTO.titulo())
                        .descripcion(pasoDTO.descripcion())
                        .fecha(pasoDTO.fecha())
                        .estado(pasoDTO.estado())
                        .inversion(inversion)
                        .build());
            }
        }
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            codigo = TokenService.randomCode(9);
        } while (inversionRepository.existsByCodigoSeguimiento(codigo));
        return codigo;
    }
}

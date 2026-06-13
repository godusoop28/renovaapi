package com.renova.renova.dto;

import com.renova.renova.model.Inversion;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Respuesta completa de una inversion. Se usa tanto para la consulta publica
 * (por codigo de seguimiento) como para el panel de administracion.
 */
public record InversionResponse(
        Long id,
        String codigoSeguimiento,
        String nombreInversionista,
        String folio,
        String propiedad,
        BigDecimal monto,
        String rendimientoEstimado,
        LocalDate fechaInicio,
        LocalDate fechaCierreEstimada,
        String asesor,
        String estado,
        boolean activo,
        Integer progreso,
        List<PasoDTO> pasos,
        List<String> imagenesAntes,
        List<String> imagenesDespues,
        Instant creadoEn,
        Instant actualizadoEn
) {
    public static InversionResponse from(Inversion inversion) {
        List<PasoDTO> pasos = inversion.getPasos().stream()
                .map(p -> new PasoDTO(p.getId(), p.getNumero(), p.getTitulo(), p.getDescripcion(), p.getFecha(), p.getEstado()))
                .toList();

        int progreso = 0;
        if (!pasos.isEmpty()) {
            long completados = pasos.stream()
                    .filter(p -> p.estado() == com.renova.renova.model.EstadoPaso.COMPLETADO)
                    .count();
            progreso = (int) Math.round((completados * 100.0) / pasos.size());
        }

        return new InversionResponse(
                inversion.getId(),
                inversion.getCodigoSeguimiento(),
                inversion.getNombreInversionista(),
                inversion.getFolio(),
                inversion.getPropiedad(),
                inversion.getMonto(),
                inversion.getRendimientoEstimado(),
                inversion.getFechaInicio(),
                inversion.getFechaCierreEstimada(),
                inversion.getAsesor(),
                inversion.getEstado(),
                inversion.isActivo(),
                progreso,
                pasos,
                inversion.getImagenesAntes(),
                inversion.getImagenesDespues(),
                inversion.getCreadoEn(),
                inversion.getActualizadoEn()
        );
    }
}

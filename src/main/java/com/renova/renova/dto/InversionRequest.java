package com.renova.renova.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Payload usado por el panel de administracion para crear o actualizar una inversion.
 * Si codigoSeguimiento se deja vacio al crear, la API genera uno automaticamente.
 */
public record InversionRequest(
        String codigoSeguimiento,
        @NotBlank String nombreInversionista,
        @NotBlank String folio,
        @NotBlank String propiedad,
        @PositiveOrZero BigDecimal monto,
        String rendimientoEstimado,
        LocalDate fechaInicio,
        LocalDate fechaCierreEstimada,
        String asesor,
        String estado,
        Boolean activo,
        @Valid List<PasoDTO> pasos,
        List<String> imagenesAntes,
        List<String> imagenesDespues
) {
}

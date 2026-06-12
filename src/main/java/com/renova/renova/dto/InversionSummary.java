package com.renova.renova.dto;

import com.renova.renova.model.Inversion;

import java.math.BigDecimal;

/** Vista resumida para el listado del panel de administracion. */
public record InversionSummary(
        Long id,
        String codigoSeguimiento,
        String nombreInversionista,
        String folio,
        String propiedad,
        BigDecimal monto,
        String estado,
        boolean activo
) {
    public static InversionSummary from(Inversion inversion) {
        return new InversionSummary(
                inversion.getId(),
                inversion.getCodigoSeguimiento(),
                inversion.getNombreInversionista(),
                inversion.getFolio(),
                inversion.getPropiedad(),
                inversion.getMonto(),
                inversion.getEstado(),
                inversion.isActivo()
        );
    }
}

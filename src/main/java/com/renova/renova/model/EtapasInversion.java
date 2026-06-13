package com.renova.renova.model;

import java.util.List;
import java.util.Optional;

/**
 * Fuente de verdad de las 6 fases definitivas del proceso de inversion.
 * El orden y los titulos deben coincidir exactamente con el frontend
 * (ver lib/inversionEtapas.ts).
 */
public final class EtapasInversion {

    public static final List<EtapaInversion> ETAPAS = List.of(
            new EtapaInversion(1, "EN_VENTA", "EN VENTA",
                    "La propiedad esta publicada y en proceso de venta en el mercado."),
            new EtapaInversion(2, "COMPRADOR_CONFIRMADO", "COMPRADOR CONFIRMADO",
                    "Ya contamos con un comprador confirmado para continuar con la operacion."),
            new EtapaInversion(3, "DICTAMINACION", "DICTAMINACIÓN",
                    "La operacion esta en proceso de dictaminacion legal y documental."),
            new EtapaInversion(4, "FIRMA_DE_ESCRITURAS", "FIRMA DE ESCRITURAS",
                    "Se esta coordinando la firma de escrituras ante notario."),
            new EtapaInversion(5, "TITULACION_DE_EXPEDIENTE", "TITULACIÓN DE EXPEDIENTE",
                    "El expediente se encuentra en proceso de titulacion a nombre del nuevo propietario."),
            new EtapaInversion(6, "CIERRE_DE_PROYECTO", "CIERRE DE PROYECTO",
                    "El proyecto esta en su etapa de cierre, incluyendo el retorno correspondiente de tu inversion.")
    );

    public static final List<String> TITULOS = ETAPAS.stream().map(EtapaInversion::titulo).toList();

    private EtapasInversion() {
    }

    public static Optional<EtapaInversion> porTitulo(String titulo) {
        if (titulo == null) return Optional.empty();
        String normalizado = titulo.trim().toUpperCase();
        return ETAPAS.stream().filter(e -> e.titulo().equals(normalizado)).findFirst();
    }

    public static boolean esTituloValido(String titulo) {
        return porTitulo(titulo).isPresent();
    }
}

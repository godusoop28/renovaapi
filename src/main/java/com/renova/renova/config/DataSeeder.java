package com.renova.renova.config;

import com.renova.renova.model.EstadoPaso;
import com.renova.renova.model.EtapasInversion;
import com.renova.renova.model.Inversion;
import com.renova.renova.model.PasoInversion;
import com.renova.renova.repository.InversionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

/** Crea una inversion de ejemplo en el primer arranque si la base de datos esta vacia. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final InversionRepository inversionRepository;

    public DataSeeder(InversionRepository inversionRepository) {
        this.inversionRepository = inversionRepository;
    }

    @Override
    public void run(String... args) {
        if (inversionRepository.count() > 0) {
            return;
        }

        Inversion inversion = Inversion.builder()
                .codigoSeguimiento("123456789")
                .nombreInversionista("Carlos M.")
                .folio("RNV-2026-0042")
                .propiedad("Av. Constitución #4521, Col. Centro, Monterrey, N.L. 64000")
                .monto(new BigDecimal("850000"))
                .rendimientoEstimado("18% anual estimado")
                .fechaInicio(LocalDate.of(2026, Month.APRIL, 15))
                .fechaCierreEstimada(LocalDate.of(2026, Month.JULY, 30))
                .asesor("Equipo Renova Norte")
                .estado("COMPRADOR CONFIRMADO")
                .activo(true)
                .build();

        inversion.getPasos().add(PasoInversion.builder()
                .numero(1).titulo(EtapasInversion.ETAPAS.get(0).titulo())
                .descripcion(EtapasInversion.ETAPAS.get(0).descripcion())
                .fecha("10 de Abril de 2026").estado(EstadoPaso.COMPLETADO).inversion(inversion).build());
        inversion.getPasos().add(PasoInversion.builder()
                .numero(2).titulo(EtapasInversion.ETAPAS.get(1).titulo())
                .descripcion(EtapasInversion.ETAPAS.get(1).descripcion())
                .fecha("Est. 30 de Junio de 2026").estado(EstadoPaso.EN_CURSO).inversion(inversion).build());
        inversion.getPasos().add(PasoInversion.builder()
                .numero(3).titulo(EtapasInversion.ETAPAS.get(2).titulo())
                .descripcion(EtapasInversion.ETAPAS.get(2).descripcion())
                .fecha("Est. 20 de Julio de 2026").estado(EstadoPaso.PENDIENTE).inversion(inversion).build());
        inversion.getPasos().add(PasoInversion.builder()
                .numero(4).titulo(EtapasInversion.ETAPAS.get(3).titulo())
                .descripcion(EtapasInversion.ETAPAS.get(3).descripcion())
                .fecha("Est. 30 de Julio de 2026").estado(EstadoPaso.PENDIENTE).inversion(inversion).build());
        inversion.getPasos().add(PasoInversion.builder()
                .numero(5).titulo(EtapasInversion.ETAPAS.get(4).titulo())
                .descripcion(EtapasInversion.ETAPAS.get(4).descripcion())
                .fecha("Est. 15 de Agosto de 2026").estado(EstadoPaso.PENDIENTE).inversion(inversion).build());
        inversion.getPasos().add(PasoInversion.builder()
                .numero(6).titulo(EtapasInversion.ETAPAS.get(5).titulo())
                .descripcion(EtapasInversion.ETAPAS.get(5).descripcion())
                .fecha("Est. 30 de Agosto de 2026").estado(EstadoPaso.PENDIENTE).inversion(inversion).build());

        inversionRepository.save(inversion);
    }
}

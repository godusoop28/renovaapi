package com.renova.renova.config;

import com.renova.renova.model.EstadoPaso;
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
                .estado("En proceso legal")
                .activo(true)
                .build();

        inversion.getPasos().add(PasoInversion.builder()
                .numero(1).titulo("Evaluación de propiedad")
                .descripcion("Se realizó la visita y avalúo de la propiedad.")
                .fecha("10 de Abril de 2026").estado(EstadoPaso.COMPLETADO).inversion(inversion).build());
        inversion.getPasos().add(PasoInversion.builder()
                .numero(2).titulo("Oferta presentada y aceptada")
                .descripcion("El vendedor aceptó los términos de la oferta.")
                .fecha("20 de Abril de 2026").estado(EstadoPaso.COMPLETADO).inversion(inversion).build());
        inversion.getPasos().add(PasoInversion.builder()
                .numero(3).titulo("Firma de convenio")
                .descripcion("Se firmó el convenio de compra-venta ante notario.")
                .fecha("28 de Abril de 2026").estado(EstadoPaso.COMPLETADO).inversion(inversion).build());
        inversion.getPasos().add(PasoInversion.builder()
                .numero(4).titulo("Proceso legal y notarial")
                .descripcion("Revisión de escrituras, liberación de adeudos y trámites notariales en curso.")
                .fecha("Est. 30 de Junio de 2026").estado(EstadoPaso.EN_CURSO).inversion(inversion).build());
        inversion.getPasos().add(PasoInversion.builder()
                .numero(5).titulo("Escrituración y cierre")
                .descripcion("Firma final ante notario y registro de la propiedad.")
                .fecha("Est. 30 de Julio de 2026").estado(EstadoPaso.PENDIENTE).inversion(inversion).build());
        inversion.getPasos().add(PasoInversion.builder()
                .numero(6).titulo("Liquidación de rendimientos")
                .descripcion("Distribución de rendimientos al inversionista.")
                .fecha("Est. 15 de Agosto de 2026").estado(EstadoPaso.PENDIENTE).inversion(inversion).build());

        inversionRepository.save(inversion);
    }
}

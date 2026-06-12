package com.renova.renova.repository;

import com.renova.renova.model.Inversion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InversionRepository extends JpaRepository<Inversion, Long> {
    Optional<Inversion> findByCodigoSeguimientoAndActivoTrue(String codigoSeguimiento);

    boolean existsByCodigoSeguimiento(String codigoSeguimiento);
}

package com.renova.renova.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inversiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Codigo / NIP de seguimiento que usa el inversionista para consultar su inversion. */
    @Column(nullable = false, unique = true, length = 30)
    private String codigoSeguimiento;

    @Column(nullable = false, length = 150)
    private String nombreInversionista;

    @Column(nullable = false, length = 50)
    private String folio;

    @Column(nullable = false, length = 255)
    private String propiedad;

    @Column(precision = 14, scale = 2)
    private BigDecimal monto;

    /** Texto libre, ej. "18% anual estimado". */
    @Column(length = 100)
    private String rendimientoEstimado;

    private LocalDate fechaInicio;

    private LocalDate fechaCierreEstimada;

    @Column(length = 150)
    private String asesor;

    /** Estado general visible para el inversionista, ej. "En proceso legal". */
    @Column(length = 100)
    private String estado;

    /** Permite ocultar la inversion sin borrarla. */
    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    @Builder.Default
    @OneToMany(mappedBy = "inversion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("numero ASC")
    @JsonManagedReference
    private List<PasoInversion> pasos = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(nullable = false)
    private Instant actualizadoEn;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        this.creadoEn = now;
        this.actualizadoEn = now;
    }

    @PreUpdate
    void preUpdate() {
        this.actualizadoEn = Instant.now();
    }
}

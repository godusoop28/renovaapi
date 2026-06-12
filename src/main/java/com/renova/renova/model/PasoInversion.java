package com.renova.renova.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pasos_inversion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasoInversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Orden del paso dentro del seguimiento (1, 2, 3...). */
    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    /** Texto libre, ej. "Est. 30 de Julio de 2026". */
    @Column(length = 100)
    private String fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPaso estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inversion_id", nullable = false)
    @JsonBackReference
    private Inversion inversion;
}

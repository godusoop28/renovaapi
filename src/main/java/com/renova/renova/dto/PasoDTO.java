package com.renova.renova.dto;

import com.renova.renova.model.EstadoPaso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasoDTO(
        Long id,
        @NotNull Integer numero,
        @NotBlank String titulo,
        String descripcion,
        String fecha,
        @NotNull EstadoPaso estado
) {
}

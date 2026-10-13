package com.cuerdas.web;

import java.time.LocalDate;

import com.cuerdas.domain.TipoGuitarra;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CuerdaSetRequest(
        @NotNull TipoGuitarra tipo,
        @NotBlank @Size(max = 500) String descripcion,
        @Min(1) Integer cantidad,
        LocalDate fechaCompra,
        LocalDate fechaCambio,
        @Size(max = 2000) String notas) {
}

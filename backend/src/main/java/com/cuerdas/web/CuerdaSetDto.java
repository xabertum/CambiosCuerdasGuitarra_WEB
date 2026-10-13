package com.cuerdas.web;

import java.time.LocalDate;

import com.cuerdas.domain.CuerdaSet;
import com.cuerdas.domain.TipoGuitarra;

public record CuerdaSetDto(
        Long id,
        TipoGuitarra tipo,
        String descripcion,
        int cantidad,
        LocalDate fechaCompra,
        LocalDate fechaCambio,
        String notas,
        String imagenUrl) {

    public static CuerdaSetDto from(CuerdaSet c) {
        // Imagen is a lazy proxy: getId() does not hit the database.
        String imagenUrl = c.getImagen() == null
                ? null
                : "/api/cuerdas/" + c.getId() + "/imagen?v=" + c.getImagen().getId();
        return new CuerdaSetDto(c.getId(), c.getTipo(), c.getDescripcion(), c.getCantidad(),
                c.getFechaCompra(), c.getFechaCambio(), c.getNotas(), imagenUrl);
    }
}

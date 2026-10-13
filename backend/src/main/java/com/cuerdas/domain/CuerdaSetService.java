package com.cuerdas.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuerdaSetService {

    private final CuerdaSetRepository cuerdas;
    private final ImagenRepository imagenes;

    public CuerdaSetService(CuerdaSetRepository cuerdas, ImagenRepository imagenes) {
        this.cuerdas = cuerdas;
        this.imagenes = imagenes;
    }

    @Transactional(readOnly = true)
    public List<CuerdaSet> listar() {
        return cuerdas.findAll(Sort.by(Sort.Order.desc("fechaCompra").nullsLast(), Sort.Order.desc("id")));
    }

    @Transactional(readOnly = true)
    public CuerdaSet obtener(Long id) {
        return cuerdas.findById(id).orElseThrow(() -> new NoEncontradoException("Set de cuerdas " + id));
    }

    public CuerdaSet crear(TipoGuitarra tipo, String descripcion, Integer cantidad,
                           LocalDate fechaCompra, LocalDate fechaCambio, String notas) {
        CuerdaSet c = new CuerdaSet();
        aplicar(c, tipo, descripcion, cantidad, fechaCompra, fechaCambio, notas);
        return cuerdas.save(c);
    }

    public CuerdaSet actualizar(Long id, TipoGuitarra tipo, String descripcion, Integer cantidad,
                                LocalDate fechaCompra, LocalDate fechaCambio, String notas) {
        CuerdaSet c = obtener(id);
        aplicar(c, tipo, descripcion, cantidad, fechaCompra, fechaCambio, notas);
        return c;
    }

    public void borrar(Long id) {
        CuerdaSet c = obtener(id);
        Imagen imagen = c.getImagen();
        cuerdas.delete(c);
        if (imagen != null) {
            imagenes.delete(imagen);
        }
    }

    @Transactional(readOnly = true)
    public Imagen imagenDe(Long id) {
        Imagen imagen = obtener(id).getImagen();
        if (imagen == null) {
            throw new NoEncontradoException("Imagen del set " + id);
        }
        imagen.getDatos(); // initialise the lazy proxy inside the transaction
        return imagen;
    }

    public CuerdaSet guardarImagen(Long id, String contentType, byte[] datos) {
        CuerdaSet c = obtener(id);
        Imagen anterior = c.getImagen();
        c.setImagen(imagenes.save(new Imagen(contentType, datos)));
        if (anterior != null) {
            imagenes.delete(anterior);
        }
        return c;
    }

    public CuerdaSet quitarImagen(Long id) {
        CuerdaSet c = obtener(id);
        Imagen anterior = c.getImagen();
        c.setImagen(null);
        if (anterior != null) {
            imagenes.delete(anterior);
        }
        return c;
    }

    private static void aplicar(CuerdaSet c, TipoGuitarra tipo, String descripcion, Integer cantidad,
                                LocalDate fechaCompra, LocalDate fechaCambio, String notas) {
        c.setTipo(tipo);
        c.setDescripcion(descripcion.strip());
        c.setCantidad(cantidad == null ? 1 : cantidad);
        c.setFechaCompra(fechaCompra);
        c.setFechaCambio(fechaCambio);
        c.setNotas(notas == null || notas.isBlank() ? null : notas.strip());
    }
}

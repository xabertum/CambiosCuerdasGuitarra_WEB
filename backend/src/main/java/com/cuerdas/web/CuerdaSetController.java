package com.cuerdas.web;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cuerdas.domain.CuerdaSetService;
import com.cuerdas.domain.Imagen;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cuerdas")
public class CuerdaSetController {

    private final CuerdaSetService service;

    public CuerdaSetController(CuerdaSetService service) {
        this.service = service;
    }

    @GetMapping
    public List<CuerdaSetDto> listar() {
        return service.listar().stream().map(CuerdaSetDto::from).toList();
    }

    @GetMapping("/{id}")
    public CuerdaSetDto obtener(@PathVariable Long id) {
        return CuerdaSetDto.from(service.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuerdaSetDto crear(@Valid @RequestBody CuerdaSetRequest r) {
        return CuerdaSetDto.from(service.crear(r.tipo(), r.descripcion(), r.cantidad(),
                r.fechaCompra(), r.fechaCambio(), r.notas()));
    }

    @PutMapping("/{id}")
    public CuerdaSetDto actualizar(@PathVariable Long id, @Valid @RequestBody CuerdaSetRequest r) {
        return CuerdaSetDto.from(service.actualizar(id, r.tipo(), r.descripcion(), r.cantidad(),
                r.fechaCompra(), r.fechaCambio(), r.notas()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        service.borrar(id);
    }

    @GetMapping("/{id}/imagen")
    public ResponseEntity<byte[]> imagen(@PathVariable Long id) {
        Imagen imagen = service.imagenDe(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imagen.getContentType()))
                // URLs carry ?v=<imagenId>, so a given URL never changes content.
                .cacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable())
                .body(imagen.getDatos());
    }

    @PutMapping(path = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CuerdaSetDto subirImagen(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo)
            throws IOException {
        String contentType = archivo.getContentType();
        if (archivo.isEmpty() || contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }
        return CuerdaSetDto.from(service.guardarImagen(id, contentType, archivo.getBytes()));
    }

    @DeleteMapping("/{id}/imagen")
    public CuerdaSetDto quitarImagen(@PathVariable Long id) {
        return CuerdaSetDto.from(service.quitarImagen(id));
    }
}

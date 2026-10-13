package com.cuerdas.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private byte[] datos;

    protected Imagen() {
    }

    public Imagen(String contentType, byte[] datos) {
        this.contentType = contentType;
        this.datos = datos;
    }

    public Long getId() { return id; }
    public String getContentType() { return contentType; }
    public byte[] getDatos() { return datos; }
}

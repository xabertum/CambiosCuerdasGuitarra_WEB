package com.cuerdas.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class CuerdaSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoGuitarra tipo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    private int cantidad = 1;

    private LocalDate fechaCompra;

    private LocalDate fechaCambio;

    @Column(length = 2000)
    private String notas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imagen_id")
    private Imagen imagen;

    public Long getId() { return id; }
    public TipoGuitarra getTipo() { return tipo; }
    public void setTipo(TipoGuitarra tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public LocalDate getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }
    public LocalDate getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDate fechaCambio) { this.fechaCambio = fechaCambio; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public Imagen getImagen() { return imagen; }
    public void setImagen(Imagen imagen) { this.imagen = imagen; }
}

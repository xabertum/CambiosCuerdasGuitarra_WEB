package com.cuerdas.domain;

public class NoEncontradoException extends RuntimeException {

    public NoEncontradoException(String que) {
        super(que + " no encontrado");
    }
}

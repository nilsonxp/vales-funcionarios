package com.evoxdev.vales_fiados_app.exception;

// Exceção para recursos não encontrados (404)
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}


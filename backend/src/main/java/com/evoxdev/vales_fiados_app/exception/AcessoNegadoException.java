package com.evoxdev.vales_fiados_app.exception;

// Exceção para acesso não autorizado (403)
public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String message) {
        super(message);
    }
}

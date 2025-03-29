package com.evoxdev.vales_fiados_app.exception;

// Exceção para regras de negócio (400)
public class NegocioException extends RuntimeException {
    public NegocioException(String message) {
        super(message);
    }
}

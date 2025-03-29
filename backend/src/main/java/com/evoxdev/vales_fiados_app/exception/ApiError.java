package com.evoxdev.vales_fiados_app.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    private String mensagem;
    private Map<String, String> erros;
    private LocalDateTime timestamp;
}
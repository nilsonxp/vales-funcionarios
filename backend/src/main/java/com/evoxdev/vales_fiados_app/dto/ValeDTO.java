package com.evoxdev.vales_fiados_app.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ValeDTO {
    private Long id;
    private BigDecimal valor;
    private String descricao;
    private boolean quitado;
    private LocalDateTime criadoEm;
    private String usuarioNome;
    private Long usuarioId;
    private String criadoPorAdm;
}
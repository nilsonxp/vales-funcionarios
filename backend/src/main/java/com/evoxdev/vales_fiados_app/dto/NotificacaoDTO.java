package com.evoxdev.vales_fiados_app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificacaoDTO {
    private Long id;
    private String mensagem;
    private LocalDateTime dataCriacao;
    private boolean lida;
    private String tipoNotificacao;
    private Long usuarioId;
    private String usuarioNome;
}
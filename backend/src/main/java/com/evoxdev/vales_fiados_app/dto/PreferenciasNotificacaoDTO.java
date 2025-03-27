package com.evoxdev.vales_fiados_app.dto;

import lombok.Data;

@Data
public class PreferenciasNotificacaoDTO {
    private Long id;
    private Long usuarioId;
    private boolean receberNotificacaoValeCriado;
    private boolean receberNotificacaoValeQuitado;
    private boolean receberResumoDiario;
    private boolean receberNotificacaoEmail;
    private boolean receberNotificacaoApp;
    private String emailAlternativo;
}
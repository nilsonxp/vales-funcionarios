package com.evoxdev.vales_fiados_app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "preferencias_notificacao")
public class PreferenciasNotificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "receber_notificacao_vale_criado", nullable = false)
    private boolean receberNotificacaoValeCriado = true;

    @Column(name = "receber_notificacao_vale_quitado", nullable = false)
    private boolean receberNotificacaoValeQuitado = true;

    @Column(name = "receber_resumo_diario", nullable = false)
    private boolean receberResumoDiario = false;

    @Column(name = "receber_notificacao_email", nullable = false)
    private boolean receberNotificacaoEmail = false;

    @Column(name = "receber_notificacao_app", nullable = false)
    private boolean receberNotificacaoApp = true;

    @Column(name = "email_alternativo")
    private String emailAlternativo;
}
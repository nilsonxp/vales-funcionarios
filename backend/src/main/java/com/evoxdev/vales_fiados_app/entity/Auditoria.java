package com.evoxdev.vales_fiados_app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoAcao; // CRIAR_VALE, QUITAR_VALE, LOGIN, etc.

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "usuario_cpf")
    private String usuarioCpf; // CPF do usuário que realizou a ação

    @Column(name = "usuario_nome")
    private String usuarioNome; // Nome do usuário que realizou a ação

    @Column(name = "ip_origem")
    private String ipOrigem;

    @Column(name = "entidade_afetada")
    private String entidadeAfetada; // Vale, Usuário, etc.

    @Column(name = "id_entidade_afetada")
    private Long idEntidadeAfetada; // ID da entidade que foi afetada

    @Column(columnDefinition = "TEXT")
    private String detalhes; // Detalhes adicionais em formato JSON se necessário
}
package com.evoxdev.vales_fiados_app.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "vales")
public class Vale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private BigDecimal valor;

    private boolean pago;

    private LocalDateTime criadoEm;

    private LocalDateTime quitadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por_id")
    private Usuario criadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Outros getters, setters e anotações se necessário
}

package com.evoxdev.vales_fiados_app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ValeDTO {
    private Long id;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "O valor deve ter no máximo 10 dígitos inteiros e 2 decimais")
    private BigDecimal valor;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    private boolean quitado;
    private LocalDateTime criadoEm;
    private String usuarioNome;
    private Long usuarioId;
    private String criadoPorAdm;
}
package com.evoxdev.vales_fiados_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerfilDTO {
    private String nome;
    private String email;
    private String telefone;
    private String role;
}

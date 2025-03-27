package com.evoxdev.vales_fiados_app.mapper;

import com.evoxdev.vales_fiados_app.dto.ValeDTO;
import com.evoxdev.vales_fiados_app.entity.Vale;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValeMapper {

    /**
     * Converte uma entidade Vale para um DTO
     */
    public ValeDTO toDTO(Vale vale) {
        if (vale == null) {
            return null;
        }

        ValeDTO dto = new ValeDTO();
        dto.setId(vale.getId());
        dto.setDescricao(vale.getDescricao());
        dto.setValor(vale.getValor());
        dto.setQuitado(vale.isPago());
        dto.setCriadoEm(vale.getCriadoEm());

        // Tratar possível NullPointerException por causa do LAZY loading
        if (vale.getUsuario() != null) {
            dto.setUsuarioId(vale.getUsuario().getId());
            dto.setUsuarioNome(vale.getUsuario().getNome());
        }

        if (vale.getCriadoPor() != null) {
            dto.setCriadoPorAdm(vale.getCriadoPor().getNome());
        }

        return dto;
    }

    /**
     * Converte uma lista de entidades Vale para uma lista de DTOs
     */
    public List<ValeDTO> toDTOList(List<Vale> vales) {
        return vales.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
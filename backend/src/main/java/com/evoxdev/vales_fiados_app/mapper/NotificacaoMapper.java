package com.evoxdev.vales_fiados_app.mapper;

import com.evoxdev.vales_fiados_app.dto.NotificacaoDTO;
import com.evoxdev.vales_fiados_app.entity.Notificacao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificacaoMapper {

    /**
     * Converte uma entidade Notificacao para um DTO
     */
    public NotificacaoDTO toDTO(Notificacao notificacao) {
        if (notificacao == null) {
            return null;
        }

        NotificacaoDTO dto = new NotificacaoDTO();
        dto.setId(notificacao.getId());
        dto.setMensagem(notificacao.getMensagem());
        dto.setDataCriacao(notificacao.getDataCriacao());
        dto.setLida(notificacao.isLida());
        dto.setTipoNotificacao(notificacao.getTipoNotificacao());

        // Tratar possível NullPointerException por causa do LAZY loading
        if (notificacao.getUsuario() != null) {
            dto.setUsuarioId(notificacao.getUsuario().getId());
            dto.setUsuarioNome(notificacao.getUsuario().getNome());
        }

        return dto;
    }

    /**
     * Converte uma lista de entidades Notificacao para uma lista de DTOs
     */
    public List<NotificacaoDTO> toDTOList(List<Notificacao> notificacoes) {
        return notificacoes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
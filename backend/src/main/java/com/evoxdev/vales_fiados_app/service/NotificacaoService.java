package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.dto.NotificacaoDTO;
import com.evoxdev.vales_fiados_app.entity.Notificacao;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.mapper.NotificacaoMapper;
import com.evoxdev.vales_fiados_app.repository.NotificacaoRepository;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoMapper notificacaoMapper;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              UsuarioRepository usuarioRepository,
                              NotificacaoMapper notificacaoMapper) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoMapper = notificacaoMapper;
    }

    /**
     * Cria uma nova notificação para um usuário
     */
    @Transactional
    public NotificacaoDTO criarNotificacao(String cpf, String mensagem, String tipoNotificacao) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(mensagem);
        notificacao.setDataCriacao(LocalDateTime.now());
        notificacao.setLida(false);
        notificacao.setTipoNotificacao(tipoNotificacao);

        notificacao = notificacaoRepository.save(notificacao);
        return notificacaoMapper.toDTO(notificacao);
    }

    /**
     * Lista todas as notificações de um usuário
     */
    @Transactional(readOnly = true)
    public List<NotificacaoDTO> listarTodasNotificacoes(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioOrderByDataCriacaoDesc(usuario);
        return notificacaoMapper.toDTOList(notificacoes);
    }

    /**
     * Lista apenas notificações não lidas de um usuário
     */
    @Transactional(readOnly = true)
    public List<NotificacaoDTO> listarNotificacoesNaoLidas(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioAndLidaFalseOrderByDataCriacaoDesc(usuario);
        return notificacaoMapper.toDTOList(notificacoes);
    }

    /**
     * Marca uma notificação específica como lida
     */
    @Transactional
    public NotificacaoDTO marcarComoLida(Long id, String cpfUsuario) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        // Verificar se a notificação pertence ao usuário
        if (!notificacao.getUsuario().getCpf().equals(cpfUsuario)) {
            throw new RuntimeException("Esta notificação não pertence ao usuário autenticado");
        }

        notificacao.setLida(true);
        notificacao = notificacaoRepository.save(notificacao);
        return notificacaoMapper.toDTO(notificacao);
    }

    /**
     * Marca todas as notificações de um usuário como lidas
     */
    @Transactional
    public void marcarTodasComoLidas(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioAndLidaFalseOrderByDataCriacaoDesc(usuario);

        notificacoes.forEach(notificacao -> notificacao.setLida(true));
        notificacaoRepository.saveAll(notificacoes);
    }

    /**
     * Conta o número de notificações não lidas
     */
    @Transactional(readOnly = true)
    public long contarNotificacoesNaoLidas(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return notificacaoRepository.countByUsuarioAndLidaFalse(usuario);
    }
}
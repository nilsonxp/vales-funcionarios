package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.dto.NotificacaoDTO;
import com.evoxdev.vales_fiados_app.dto.WebSocketMessage;
import com.evoxdev.vales_fiados_app.entity.Notificacao;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.mapper.NotificacaoMapper;
import com.evoxdev.vales_fiados_app.repository.NotificacaoRepository;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoMapper notificacaoMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              UsuarioRepository usuarioRepository,
                              NotificacaoMapper notificacaoMapper,
                              SimpMessagingTemplate messagingTemplate) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoMapper = notificacaoMapper;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Cria uma nova notificação para um usuário e envia via WebSocket
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
        NotificacaoDTO notificacaoDTO = notificacaoMapper.toDTO(notificacao);

        // Enviar notificação em tempo real para o usuário específico
        WebSocketMessage wsMessage = new WebSocketMessage("NOTIFICACAO", notificacaoDTO);
        messagingTemplate.convertAndSendToUser(
                cpf,  // o nome de usuário para STOMP é o CPF do usuário
                "/queue/notificacoes",
                wsMessage
        );

        return notificacaoDTO;
    }

    // Resto dos métodos permanece igual...
    @Transactional(readOnly = true)
    public List<NotificacaoDTO> listarTodasNotificacoes(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioOrderByDataCriacaoDesc(usuario);
        return notificacaoMapper.toDTOList(notificacoes);
    }

    @Transactional(readOnly = true)
    public List<NotificacaoDTO> listarNotificacoesNaoLidas(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioAndLidaFalseOrderByDataCriacaoDesc(usuario);
        return notificacaoMapper.toDTOList(notificacoes);
    }

    @Transactional
    public NotificacaoDTO marcarComoLida(Long id, String cpfUsuario) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        if (!notificacao.getUsuario().getCpf().equals(cpfUsuario)) {
            throw new RuntimeException("Esta notificação não pertence ao usuário autenticado");
        }

        notificacao.setLida(true);
        notificacao = notificacaoRepository.save(notificacao);

        // Enviar atualização em tempo real
        NotificacaoDTO notificacaoDTO = notificacaoMapper.toDTO(notificacao);
        WebSocketMessage wsMessage = new WebSocketMessage("NOTIFICACAO_LIDA", notificacaoDTO);
        messagingTemplate.convertAndSendToUser(
                cpfUsuario,
                "/queue/notificacoes",
                wsMessage
        );

        return notificacaoDTO;
    }

    @Transactional
    public void marcarTodasComoLidas(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioAndLidaFalseOrderByDataCriacaoDesc(usuario);

        notificacoes.forEach(notificacao -> notificacao.setLida(true));
        notificacaoRepository.saveAll(notificacoes);

        // Enviar atualização em tempo real
        WebSocketMessage wsMessage = new WebSocketMessage("TODAS_NOTIFICACOES_LIDAS", null);
        messagingTemplate.convertAndSendToUser(
                cpf,
                "/queue/notificacoes",
                wsMessage
        );
    }

    @Transactional(readOnly = true)
    public long contarNotificacoesNaoLidas(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return notificacaoRepository.countByUsuarioAndLidaFalse(usuario);
    }
}
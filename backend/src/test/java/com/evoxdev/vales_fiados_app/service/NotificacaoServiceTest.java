package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.dto.NotificacaoDTO;
import com.evoxdev.vales_fiados_app.dto.WebSocketMessage;
import com.evoxdev.vales_fiados_app.entity.Notificacao;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.mapper.NotificacaoMapper;
import com.evoxdev.vales_fiados_app.repository.NotificacaoRepository;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificacaoServiceTest {

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private NotificacaoMapper notificacaoMapper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private PreferenciasNotificacaoService preferenciasService;

    @InjectMocks
    private NotificacaoService notificacaoService;

    private Usuario usuario;
    private Notificacao notificacao;
    private NotificacaoDTO notificacaoDTO;
    private List<Notificacao> notificacoes;

    @BeforeEach
    void setUp() {
        // Configurar objetos para testes
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setCpf("12345678901");
        usuario.setRole("USER");

        notificacao = new Notificacao();
        notificacao.setId(1L);
        notificacao.setUsuario(usuario);
        notificacao.setMensagem("Teste de notificação");
        notificacao.setDataCriacao(LocalDateTime.now());
        notificacao.setLida(false);
        notificacao.setTipoNotificacao("VALE_CRIADO");

        notificacaoDTO = new NotificacaoDTO();
        notificacaoDTO.setId(1L);
        notificacaoDTO.setMensagem("Teste de notificação");
        notificacaoDTO.setDataCriacao(LocalDateTime.now());
        notificacaoDTO.setLida(false);
        notificacaoDTO.setTipoNotificacao("VALE_CRIADO");
        notificacaoDTO.setUsuarioId(1L);
        notificacaoDTO.setUsuarioNome("Usuário Teste");

        notificacoes = new ArrayList<>();
        notificacoes.add(notificacao);
    }

    @Test
    void testCriarNotificacao() {
        // Configuração
        when(preferenciasService.deveReceberNotificacao(usuario.getCpf(), "VALE_CRIADO")).thenReturn(true);
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(notificacaoRepository.save(any(Notificacao.class))).thenReturn(notificacao);
        when(notificacaoMapper.toDTO(any(Notificacao.class))).thenReturn(notificacaoDTO);

        // Execução
        NotificacaoDTO resultado = notificacaoService.criarNotificacao(usuario.getCpf(), "Teste de notificação", "VALE_CRIADO");

        // Verificação
        assertNotNull(resultado);
        assertEquals(notificacaoDTO.getId(), resultado.getId());
        assertEquals(notificacaoDTO.getMensagem(), resultado.getMensagem());

        // Verificar se o objeto correto foi enviado via WebSocket
        ArgumentCaptor<WebSocketMessage> captor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messagingTemplate).convertAndSendToUser(eq(usuario.getCpf()), eq("/queue/notificacoes"), captor.capture());
        WebSocketMessage mensagemCapturada = captor.getValue();
        assertEquals("NOTIFICACAO", mensagemCapturada.getTipo());
        assertEquals(notificacaoDTO, mensagemCapturada.getConteudo());
    }

    @Test
    void testNaoCriarNotificacaoQuandoPreferenciasDesativadas() {
        // Configuração
        when(preferenciasService.deveReceberNotificacao(usuario.getCpf(), "VALE_CRIADO")).thenReturn(false);

        // Execução
        NotificacaoDTO resultado = notificacaoService.criarNotificacao(usuario.getCpf(), "Teste de notificação", "VALE_CRIADO");

        // Verificação
        assertNull(resultado);
        verify(usuarioRepository, never()).findByCpf(anyString());
        verify(notificacaoRepository, never()).save(any(Notificacao.class));
        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
    }

    @Test
    void testListarTodasNotificacoes() {
        // Configuração
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(notificacaoRepository.findByUsuarioOrderByDataCriacaoDesc(usuario)).thenReturn(notificacoes);
        when(notificacaoMapper.toDTOList(notificacoes)).thenReturn(List.of(notificacaoDTO));

        // Execução
        List<NotificacaoDTO> resultado = notificacaoService.listarTodasNotificacoes(usuario.getCpf());

        // Verificação
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(notificacaoDTO.getId(), resultado.get(0).getId());
    }

    @Test
    void testListarNotificacoesNaoLidas() {
        // Configuração
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(notificacaoRepository.findByUsuarioAndLidaFalseOrderByDataCriacaoDesc(usuario)).thenReturn(notificacoes);
        when(notificacaoMapper.toDTOList(notificacoes)).thenReturn(List.of(notificacaoDTO));

        // Execução
        List<NotificacaoDTO> resultado = notificacaoService.listarNotificacoesNaoLidas(usuario.getCpf());

        // Verificação
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(notificacaoDTO.getId(), resultado.get(0).getId());
    }

    @Test
    void testMarcarComoLida() {
        // Configuração
        when(notificacaoRepository.findById(1L)).thenReturn(Optional.of(notificacao));
        when(notificacaoRepository.save(notificacao)).thenReturn(notificacao);
        when(notificacaoMapper.toDTO(notificacao)).thenReturn(notificacaoDTO);

        // Execução
        NotificacaoDTO resultado = notificacaoService.marcarComoLida(1L, usuario.getCpf());

        // Verificação
        assertNotNull(resultado);
        verify(notificacaoRepository).save(notificacao);
        assertTrue(notificacao.isLida());

        // Verificar se WebSocket foi chamado
        verify(messagingTemplate).convertAndSendToUser(eq(usuario.getCpf()), eq("/queue/notificacoes"), any(WebSocketMessage.class));
    }

    @Test
    void testMarcarTodasComoLidas() {
        // Configuração
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(notificacaoRepository.findByUsuarioAndLidaFalseOrderByDataCriacaoDesc(usuario)).thenReturn(notificacoes);

        // Execução
        notificacaoService.marcarTodasComoLidas(usuario.getCpf());

        // Verificação
        assertTrue(notificacao.isLida());
        verify(notificacaoRepository).saveAll(notificacoes);

        // Verificar se WebSocket foi chamado
        verify(messagingTemplate).convertAndSendToUser(eq(usuario.getCpf()), eq("/queue/notificacoes"), any(WebSocketMessage.class));
    }

    @Test
    void testContarNotificacoesNaoLidas() {
        // Configuração
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(notificacaoRepository.countByUsuarioAndLidaFalse(usuario)).thenReturn(5L);

        // Execução
        long resultado = notificacaoService.contarNotificacoesNaoLidas(usuario.getCpf());

        // Verificação
        assertEquals(5L, resultado);
    }
}
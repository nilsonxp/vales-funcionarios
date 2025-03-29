package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.dto.PreferenciasNotificacaoDTO;
import com.evoxdev.vales_fiados_app.entity.PreferenciasNotificacao;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.repository.PreferenciasNotificacaoRepository;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PreferenciasNotificacaoServiceTest {

    @Mock
    private PreferenciasNotificacaoRepository preferenciaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private PreferenciasNotificacaoService preferenciaService;

    private Usuario usuario;
    private PreferenciasNotificacao preferencias;
    private PreferenciasNotificacaoDTO preferenciasDTO;

    @BeforeEach
    void setUp() {
        // Configurar objetos para testes
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setCpf("12345678901");
        usuario.setEmail("usuario@teste.com");
        usuario.setRole("USER");

        preferencias = new PreferenciasNotificacao();
        preferencias.setId(1L);
        preferencias.setUsuario(usuario);
        preferencias.setReceberNotificacaoValeCriado(true);
        preferencias.setReceberNotificacaoValeQuitado(true);
        preferencias.setReceberResumoDiario(false);
        preferencias.setReceberNotificacaoEmail(false);
        preferencias.setReceberNotificacaoApp(true);
        preferencias.setEmailAlternativo("usuario@teste.com");

        preferenciasDTO = new PreferenciasNotificacaoDTO();
        preferenciasDTO.setId(1L);
        preferenciasDTO.setUsuarioId(1L);
        preferenciasDTO.setReceberNotificacaoValeCriado(true);
        preferenciasDTO.setReceberNotificacaoValeQuitado(true);
        preferenciasDTO.setReceberResumoDiario(false);
        preferenciasDTO.setReceberNotificacaoEmail(false);
        preferenciasDTO.setReceberNotificacaoApp(true);
        preferenciasDTO.setEmailAlternativo("usuario@teste.com");
    }

    @Test
    void testObterPreferenciasPorCpf() {
        // Configuração
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(preferenciaRepository.findByUsuario(usuario)).thenReturn(Optional.of(preferencias));

        // Execução
        PreferenciasNotificacaoDTO resultado = preferenciaService.obterPreferenciasPorCpf(usuario.getCpf());

        // Verificação
        assertNotNull(resultado);
        assertEquals(preferencias.getId(), resultado.getId());
        assertEquals(preferencias.getUsuario().getId(), resultado.getUsuarioId());
        assertEquals(preferencias.isReceberNotificacaoValeCriado(), resultado.isReceberNotificacaoValeCriado());
    }

    @Test
    void testCriarPreferenciasDefault() {
        // Configuração
        when(preferenciaRepository.existsByUsuario(usuario)).thenReturn(false);
        when(preferenciaRepository.save(any(PreferenciasNotificacao.class))).thenReturn(preferencias);

        // Execução
        PreferenciasNotificacao resultado = preferenciaService.criarPreferenciasDefault(usuario);

        // Verificação
        assertNotNull(resultado);
        verify(preferenciaRepository).save(any(PreferenciasNotificacao.class));
    }

    @Test
    void testAtualizarPreferencias() {
        // Configuração
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(preferenciaRepository.findByUsuario(usuario)).thenReturn(Optional.of(preferencias));
        when(preferenciaRepository.save(any(PreferenciasNotificacao.class))).thenReturn(preferencias);

        // Modificar o DTO
        preferenciasDTO.setReceberNotificacaoValeCriado(false);
        preferenciasDTO.setReceberNotificacaoEmail(true);

        // Execução
        PreferenciasNotificacaoDTO resultado = preferenciaService.atualizarPreferencias(usuario.getCpf(), preferenciasDTO);

        // Verificação
        assertNotNull(resultado);
        assertEquals(false, resultado.isReceberNotificacaoValeCriado());
        assertEquals(true, resultado.isReceberNotificacaoEmail());

        // Verificar se os valores foram atualizados na entidade e salvos
        verify(preferenciaRepository).save(preferencias);
        assertEquals(false, preferencias.isReceberNotificacaoValeCriado());
        assertEquals(true, preferencias.isReceberNotificacaoEmail());

        // Verificar se a auditoria foi registrada
        verify(auditoriaService).registrarAcao(
                eq("ATUALIZAR_PREFERENCIAS_NOTIFICACAO"),
                anyString(),
                eq("Usuario"),
                eq(usuario.getId()),
                anyString()
        );
    }

    @Test
    void testDeveReceberNotificacao() {
        // Configuração
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(preferenciaRepository.findByUsuario(usuario)).thenReturn(Optional.of(preferencias));

        // Execução e Verificação
        // Caso 1: Quando usuário desativa notificações no app
        preferencias.setReceberNotificacaoApp(false);
        boolean resultado1 = preferenciaService.deveReceberNotificacao(usuario.getCpf(), "VALE_CRIADO");
        assertFalse(resultado1);

        // Caso 2: Quando usuário desativa notificações de vale criado
        preferencias.setReceberNotificacaoApp(true);
        preferencias.setReceberNotificacaoValeCriado(false);
        boolean resultado2 = preferenciaService.deveReceberNotificacao(usuario.getCpf(), "VALE_CRIADO");
        assertFalse(resultado2);

        // Caso 3: Quando usuário tem notificações de vale criado ativadas
        preferencias.setReceberNotificacaoValeCriado(true);
        boolean resultado3 = preferenciaService.deveReceberNotificacao(usuario.getCpf(), "VALE_CRIADO");
        assertTrue(resultado3);

        // Caso 4: Quando o tipo de notificação é outro
        boolean resultado4 = preferenciaService.deveReceberNotificacao(usuario.getCpf(), "OUTRO_TIPO");
        assertTrue(resultado4);
    }

    @Test
    void testDeveReceberNotificacaoQuandoPreferenciasNaoExistem() {
        // Configuração
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(preferenciaRepository.findByUsuario(usuario)).thenReturn(Optional.empty());
        when(preferenciaRepository.save(any(PreferenciasNotificacao.class))).thenReturn(preferencias);

        // Execução
        boolean resultado = preferenciaService.deveReceberNotificacao(usuario.getCpf(), "VALE_CRIADO");

        // Verificação
        assertTrue(resultado);
        verify(preferenciaRepository).save(any(PreferenciasNotificacao.class));
    }
}
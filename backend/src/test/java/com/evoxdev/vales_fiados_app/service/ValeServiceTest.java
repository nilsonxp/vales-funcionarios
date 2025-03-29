package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.dto.ValeDTO;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.entity.Vale;
import com.evoxdev.vales_fiados_app.exception.NegocioException;
import com.evoxdev.vales_fiados_app.exception.RecursoNaoEncontradoException;
import com.evoxdev.vales_fiados_app.mapper.ValeMapper;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import com.evoxdev.vales_fiados_app.repository.ValeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ValeServiceTest {

    @Mock
    private ValeRepository valeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ValeMapper valeMapper;

    @Mock
    private NotificacaoService notificacaoService;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private ValeService valeService;

    private AutoCloseable closeable;
    private Usuario usuario;
    private Usuario admin;
    private Vale vale;
    private ValeDTO valeDTO;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Configurar objetos para testes
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João");
        usuario.setCpf("12345678901");
        usuario.setRole("USER");

        admin = new Usuario();
        admin.setId(2L);
        admin.setNome("Admin");
        admin.setCpf("98765432101");
        admin.setRole("ADMIN");

        vale = new Vale();
        vale.setId(1L);
        vale.setDescricao("Teste de vale");
        vale.setValor(new BigDecimal("100.00"));
        vale.setPago(false);
        vale.setCriadoEm(LocalDateTime.now());
        vale.setUsuario(usuario);
        vale.setCriadoPor(admin);

        valeDTO = new ValeDTO();
        valeDTO.setId(1L);
        valeDTO.setDescricao("Teste de vale");
        valeDTO.setValor(new BigDecimal("100.00"));
        valeDTO.setQuitado(false);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testMarcarComoPago() {
        // Given
        when(valeRepository.findById(1L)).thenReturn(Optional.of(vale));
        when(valeRepository.save(any(Vale.class))).thenReturn(vale);
        when(valeMapper.toDTO(any(Vale.class))).thenReturn(valeDTO);

        // When
        ValeDTO result = valeService.marcarComoPago(1L);

        // Then
        assertNotNull(result);
        verify(valeRepository).findById(1L);
        verify(valeRepository).save(any(Vale.class));
        verify(notificacaoService).criarNotificacao(eq(usuario.getCpf()), anyString(), eq("VALE_QUITADO"));
        verify(auditoriaService).registrarAcao(eq("QUITAR_VALE"), anyString(), eq("Vale"), any(), anyString());
    }

    @Test
    void testCriarVale() {
        // Given
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByCpf(admin.getCpf())).thenReturn(Optional.of(admin));
        when(valeRepository.save(any(Vale.class))).thenReturn(vale);
        when(valeMapper.toDTO(any(Vale.class))).thenReturn(valeDTO);

        // When
        ValeDTO result = valeService.criarVale(usuario.getCpf(), valeDTO, admin.getCpf());

        // Then
        assertNotNull(result);
        verify(usuarioRepository).findByCpf(usuario.getCpf());
        verify(usuarioRepository).findByCpf(admin.getCpf());
        verify(valeRepository).save(any(Vale.class));
        verify(notificacaoService).criarNotificacao(eq(usuario.getCpf()), anyString(), eq("VALE_CRIADO"));
        verify(auditoriaService).registrarAcao(eq("CRIAR_VALE"), anyString(), eq("Vale"), any(), anyString());
    }

    @Test
    void testUsuarioNaoEncontrado() {
        // Given
        when(usuarioRepository.findByCpf(anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            valeService.listarDTOsPorUsuario("cpf-inexistente");
        });

        verify(usuarioRepository).findByCpf("cpf-inexistente");
        verify(valeRepository, never()).findByUsuario(any(Usuario.class));
    }

    @Test
    void testListarDTOsPorUsuario() {
        // Given
        List<Vale> vales = new ArrayList<>();
        vales.add(vale);

        List<ValeDTO> dtos = new ArrayList<>();
        dtos.add(valeDTO);

        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(valeRepository.findByUsuario(usuario)).thenReturn(vales);
        when(valeMapper.toDTOList(vales)).thenReturn(dtos);

        // When
        List<ValeDTO> result = valeService.listarDTOsPorUsuario(usuario.getCpf());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(valeDTO.getId(), result.get(0).getId());

        verify(usuarioRepository).findByCpf(usuario.getCpf());
        verify(valeRepository).findByUsuario(usuario);
        verify(valeMapper).toDTOList(vales);
        verify(auditoriaService).registrarAcao(eq("CONSULTAR_VALES"), anyString(), eq("Usuario"), eq(usuario.getId()), anyString());
    }

    @Test
    void testValeJaPago() {
        // Given
        vale.setPago(true);
        when(valeRepository.findById(1L)).thenReturn(Optional.of(vale));

        // When/Then
        assertThrows(NegocioException.class, () -> {
            valeService.marcarComoPago(1L);
        });

        verify(valeRepository).findById(1L);
        verify(valeRepository, never()).save(any(Vale.class));
    }

    @Test
    void testValorInvalido() {
        // Given
        valeDTO.setValor(BigDecimal.ZERO);
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByCpf(admin.getCpf())).thenReturn(Optional.of(admin));

        // When/Then
        assertThrows(NegocioException.class, () -> {
            valeService.criarVale(usuario.getCpf(), valeDTO, admin.getCpf());
        });

        verify(usuarioRepository).findByCpf(usuario.getCpf());
        verify(usuarioRepository).findByCpf(admin.getCpf());
        verify(valeRepository, never()).save(any(Vale.class));
    }

    @Test
    void testDescricaoVazia() {
        // Given
        valeDTO.setDescricao("");
        when(usuarioRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByCpf(admin.getCpf())).thenReturn(Optional.of(admin));

        // When/Then
        assertThrows(NegocioException.class, () -> {
            valeService.criarVale(usuario.getCpf(), valeDTO, admin.getCpf());
        });

        verify(usuarioRepository).findByCpf(usuario.getCpf());
        verify(usuarioRepository).findByCpf(admin.getCpf());
        verify(valeRepository, never()).save(any(Vale.class));
    }
}
package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.dto.ValeDTO;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.entity.Vale;
import com.evoxdev.vales_fiados_app.mapper.ValeMapper;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import com.evoxdev.vales_fiados_app.repository.ValeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ValeService {

    private final ValeRepository valeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ValeMapper valeMapper;
    private final NotificacaoService notificacaoService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ValeService(ValeRepository valeRepository,
                       UsuarioRepository usuarioRepository,
                       ValeMapper valeMapper,
                       NotificacaoService notificacaoService) {
        this.valeRepository = valeRepository;
        this.usuarioRepository = usuarioRepository;
        this.valeMapper = valeMapper;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public ValeDTO criarVale(String cpf, ValeDTO dto, String criadoPorCpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Usuario criadoPor = usuarioRepository.findByCpf(criadoPorCpf)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

        Vale vale = new Vale();
        vale.setUsuario(usuario);
        vale.setDescricao(dto.getDescricao());
        vale.setValor(dto.getValor());
        vale.setPago(false);
        vale.setCriadoEm(LocalDateTime.now());
        vale.setCriadoPor(criadoPor);

        vale = valeRepository.save(vale);

        // Criar notificação para o usuário
        String valorFormatado = vale.getValor().toString();
        String mensagem = String.format(
                "Um novo vale no valor de R$ %s foi criado para você por %s. Descrição: %s",
                valorFormatado,
                criadoPor.getNome(),
                vale.getDescricao()
        );

        notificacaoService.criarNotificacao(
                usuario.getCpf(),
                mensagem,
                "VALE_CRIADO"
        );

        return valeMapper.toDTO(vale);
    }

    @Transactional(readOnly = true)
    public List<ValeDTO> listarDTOsPorUsuario(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        List<Vale> vales = valeRepository.findByUsuario(usuario);
        return valeMapper.toDTOList(vales);
    }

    @Transactional
    public ValeDTO marcarComoPago(Long id) {
        Vale vale = valeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vale não encontrado"));

        vale.setPago(true);
        vale.setQuitadoEm(LocalDateTime.now());

        vale = valeRepository.save(vale);

        // Criar notificação para o usuário
        String valorFormatado = vale.getValor().toString();
        String dataQuitacao = vale.getQuitadoEm().format(DATE_FORMATTER);

        String mensagem = String.format(
                "Seu vale no valor de R$ %s (descrição: %s) foi quitado em %s",
                valorFormatado,
                vale.getDescricao(),
                dataQuitacao
        );

        notificacaoService.criarNotificacao(
                vale.getUsuario().getCpf(),
                mensagem,
                "VALE_QUITADO"
        );

        return valeMapper.toDTO(vale);
    }
}
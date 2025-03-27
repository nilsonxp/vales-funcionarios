package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.dto.ValeDTO;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.entity.Vale;
import com.evoxdev.vales_fiados_app.mapper.ValeMapper;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import com.evoxdev.vales_fiados_app.repository.ValeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ValeService {

    private final ValeRepository valeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ValeMapper valeMapper;

    public ValeService(ValeRepository valeRepository,
                       UsuarioRepository usuarioRepository,
                       ValeMapper valeMapper) {
        this.valeRepository = valeRepository;
        this.usuarioRepository = usuarioRepository;
        this.valeMapper = valeMapper;
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
        return valeMapper.toDTO(vale);
    }
}
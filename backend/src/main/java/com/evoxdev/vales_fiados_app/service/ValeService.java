package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.dto.ValeDTO;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.entity.Vale;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import com.evoxdev.vales_fiados_app.repository.ValeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ValeService {

    private final ValeRepository valeRepository;
    private final UsuarioRepository usuarioRepository;

    public ValeService(ValeRepository valeRepository, UsuarioRepository usuarioRepository) {
        this.valeRepository = valeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Vale criarVale(String cpf, ValeDTO dto, String criadoPorCpf) {
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

        return valeRepository.save(vale);
    }

    public List<Vale> listarPorUsuario(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return valeRepository.findByUsuario(usuario);
    }

    public void marcarComoPago(Long id) {
        Vale vale = valeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vale não encontrado"));

        vale.setPago(true);
        vale.setQuitadoEm(LocalDateTime.now());

        valeRepository.save(vale);
    }
}

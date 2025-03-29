package com.evoxdev.vales_fiados_app.repository;

import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.entity.Vale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ValeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ValeRepository valeRepository;

    @Test
    void deveBuscarValesPorUsuario() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setNome("Teste Usuario 1");
        usuario1.setCpf("12345678901");
        usuario1.setSenha("senha123");
        usuario1.setRole("USER");
        entityManager.persist(usuario1);

        Usuario usuario2 = new Usuario();
        usuario2.setNome("Teste Usuario 2");
        usuario2.setCpf("98765432101");
        usuario2.setSenha("senha123");
        usuario2.setRole("USER");
        entityManager.persist(usuario2);

        Usuario admin = new Usuario();
        admin.setNome("Admin");
        admin.setCpf("11111111111");
        admin.setSenha("admin123");
        admin.setRole("ADMIN");
        entityManager.persist(admin);

        // Criar vales para o primeiro usuário
        Vale vale1 = new Vale();
        vale1.setDescricao("Vale 1 do usuário 1");
        vale1.setValor(new BigDecimal("100.00"));
        vale1.setPago(false);
        vale1.setCriadoEm(LocalDateTime.now());
        vale1.setUsuario(usuario1);
        vale1.setCriadoPor(admin);
        entityManager.persist(vale1);

        Vale vale2 = new Vale();
        vale2.setDescricao("Vale 2 do usuário 1");
        vale2.setValor(new BigDecimal("200.00"));
        vale2.setPago(true);
        vale2.setCriadoEm(LocalDateTime.now().minusDays(1));
        vale2.setQuitadoEm(LocalDateTime.now());
        vale2.setUsuario(usuario1);
        vale2.setCriadoPor(admin);
        entityManager.persist(vale2);

        // Criar vale para o segundo usuário
        Vale vale3 = new Vale();
        vale3.setDescricao("Vale do usuário 2");
        vale3.setValor(new BigDecimal("150.00"));
        vale3.setPago(false);
        vale3.setCriadoEm(LocalDateTime.now());
        vale3.setUsuario(usuario2);
        vale3.setCriadoPor(admin);
        entityManager.persist(vale3);

        entityManager.flush();

        // Act
        List<Vale> valesUsuario1 = valeRepository.findByUsuario(usuario1);
        List<Vale> valesUsuario2 = valeRepository.findByUsuario(usuario2);

        // Assert
        assertEquals(2, valesUsuario1.size());
        assertEquals(1, valesUsuario2.size());

        // Verificar detalhes do primeiro vale do usuário 1
        Vale primeiroVale = valesUsuario1.get(0);
        assertNotNull(primeiroVale.getId());
        assertEquals("Vale 1 do usuário 1", primeiroVale.getDescricao());
        assertEquals(new BigDecimal("100.00"), primeiroVale.getValor());
        assertEquals(false, primeiroVale.isPago());

        // Verificar o vale do usuário 2
        Vale valeUsuario2 = valesUsuario2.get(0);
        assertEquals("Vale do usuário 2", valeUsuario2.getDescricao());
        assertEquals(new BigDecimal("150.00"), valeUsuario2.getValor());
    }
}
package com.evoxdev.vales_fiados_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Desabilita CSRF para APIs REST
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/usuarios/cadastrar").permitAll()  // Permite acesso ao endpoint de cadastro
                        .anyRequest().authenticated()  // Outras requisições precisam de autenticação
                )
                .httpBasic(withDefaults());  // Usa autenticação básica HTTP

        return http.build();
    }
}
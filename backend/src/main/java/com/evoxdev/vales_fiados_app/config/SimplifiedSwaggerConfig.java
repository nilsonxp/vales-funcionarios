package com.evoxdev.vales_fiados_app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimplifiedSwaggerConfig {

    @Bean
    public OpenAPI simplifiedOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Vales para Funcionários")
                        .description("Sistema de gerenciamento de vales para funcionários")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
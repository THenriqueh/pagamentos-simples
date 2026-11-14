package com.sistema.pagamentos;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "API de Pagamentos",
        version = "1.0",
        description = "Sistema de pagamentos estilo PIX"))
public class OpenApiConfig {
}

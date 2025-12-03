package com.example.subject.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Subject Service API")
                        .version("1.0")
                        .description("API documentation for Subject Service")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                // 1. Định nghĩa Security Scheme (Bearer Token)
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                // 2. Áp dụng bảo mật này cho toàn bộ API
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
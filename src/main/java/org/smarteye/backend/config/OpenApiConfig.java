package org.smarteye.backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartEyeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartEye Backend API")
                        .description("API v1 — tech_pallets, measurements, weights (BEFORE/AFTER), files (MinIO), defects, operator actions, security (JWT), WebSocket, отчёты")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SmartEye")
                                .url("https://example.org/smarteye")
                                .email("support@example.org"))
                        .license(new License().name("Proprietary").url("https://example.org/license")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local"),
                        new Server().url("http://backend:8080").description("Docker network")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("OpenAPI JSON")
                        .url("/v3/api-docs"));
    }
}

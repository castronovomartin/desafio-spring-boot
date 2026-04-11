package com.nuevospa.taskmanager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

   private static final String SECURITY_SCHEME_NAME = "bearerAuth";

   @Bean
   public OpenAPI openAPI() {
      return new OpenAPI()
            .info(buildInfo())
            .addSecurityItem(new SecurityRequirement()
                  .addList(SECURITY_SCHEME_NAME))
            .components(new Components()
                  .addSecuritySchemes(SECURITY_SCHEME_NAME,
                        buildSecurityScheme()));
   }

   private Info buildInfo() {
      return new Info()
            .title("Task Manager API")
            .description("API RESTful para gestión de tareas - Desafío Técnico Nuevo SPA")
            .version("1.0.0")
            .contact(new Contact()
                  .name("Martin Castronovo")
                  .email("tinchocastronovo@gmail.com"));
   }

   private SecurityScheme buildSecurityScheme() {
      return new SecurityScheme()
            .name(SECURITY_SCHEME_NAME)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Ingresá el JWT token obtenido del endpoint /auth/login");
   }
}

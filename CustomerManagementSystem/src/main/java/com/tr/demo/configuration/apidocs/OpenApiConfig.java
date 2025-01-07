package com.tr.demo.configuration.apidocs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Customer Management System API", version = "v0.01", description = "API Documentation"),
        security = @SecurityRequirement(name = "CustomerAuth")
)
@SecurityScheme(
        name = "CustomerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Authentication with Bearer token"
)
public class OpenApiConfig {

}

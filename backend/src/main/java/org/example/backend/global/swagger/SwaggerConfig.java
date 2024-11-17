package org.example.backend.global.swagger;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private Info info() {
        String description = "<h3>필독!</h3>  \n" +
                "Dealivery Swagger";
        return new Info().title("Dealivery").description(description).version("1.0.0");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info());
    }

}

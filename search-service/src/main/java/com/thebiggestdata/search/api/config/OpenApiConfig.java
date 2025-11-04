package com.thebiggestdata.search.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI searchServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Search Service API")
                        .description("Microservicio de búsqueda para The Biggest Data Search Engine - Stage 2")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("The Biggest Data Team")
                                .url("https://github.com/thebiggestdata/stage_2")));
    }
}
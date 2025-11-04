package com.thebiggestdata.indexing.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI indexingServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8081");
        devServer.setDescription("Dev server");

        Contact contact = new Contact();
        contact.setUrl("https://github.com/thebiggestdata/stage_2");
        contact.setName("The biggest data team");

        Info info = new Info()
                .title("Indexing Service API")
                .version("1.0.0")
                .contact(contact)
                .description("REST API for the indexing service of a search engine");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
package api.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {
    @Bean
    public OpenAPI ingestionOpenApi() {
        return new OpenAPI();
    }
}

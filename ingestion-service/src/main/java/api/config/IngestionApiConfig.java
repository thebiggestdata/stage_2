package api.config;

import control.utils.BookStorageRepository;
import control.utils.FileSystemBookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.file.Paths;

@Configuration
public class IngestionApiConfig implements WebMvcConfigurer {

    @Value("${datalake.base-path}")
    private String datalakeBasePath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public BookStorageRepository bookStorageRepository() {
        String absolutePath = Paths.get(datalakeBasePath).toAbsolutePath().normalize().toString();
        return new FileSystemBookRepository(absolutePath);
    }
}
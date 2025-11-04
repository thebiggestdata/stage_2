package api.config;

import control.CrawlerController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.file.Paths;

@Configuration
public class CrawlerConfig {

    @Value("${datalake.base-path}")
    private String datalakeBasePath;

    @Bean
    public CrawlerController crawlerController() {
        control.utils.CrawlerConfig config = new control.utils.CrawlerConfig(1, 1000, 1000L);
        // Convertir ruta relativa a absoluta
        String absolutePath = Paths.get(datalakeBasePath).toAbsolutePath().normalize().toString();
        return new CrawlerController(config, absolutePath);
    }
}
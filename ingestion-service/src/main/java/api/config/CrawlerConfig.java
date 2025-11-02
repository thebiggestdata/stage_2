package api.config;

import control.CrawlerController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrawlerConfig {

    @Value("${datalake.base-path}")
    private String datalakeBasePath;

    @Bean
    public CrawlerController crawlerController() {
        control.utils.CrawlerConfig config = new control.utils.CrawlerConfig(1, 1000, 1000L);
        return new CrawlerController(config, datalakeBasePath);
    }
}

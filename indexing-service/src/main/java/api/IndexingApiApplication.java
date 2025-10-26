package api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"api", "indexing", "metadata"})
public class IndexingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndexingApiApplication.class, args);
    }
}
package com.thebiggestdata.ingestion.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class IngestionApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IngestionApiApplication.class, args);
    }
}

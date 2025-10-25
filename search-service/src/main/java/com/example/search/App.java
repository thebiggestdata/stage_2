package com.example.search;

import com.example.search.controller.SearchController;
import com.example.search.repository.InvertedIndexRepository;
import com.example.search.service.SearchService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        int port = getEnvInt("SEARCH_PORT", 7003);
        String datamartPath = getEnv("DATAMART_PATH", "./datamart");

        logger.info("Starting Search Service...");
        logger.info("Port: {}", port);
        logger.info("Datamart path: {}", datamartPath);

        InvertedIndexRepository repository = new InvertedIndexRepository(datamartPath);
        SearchService searchService = new SearchService(repository);
        SearchController controller = new SearchController(searchService);

        // Configurar Gson como JSON mapper
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
            config.showJavalinBanner = false;

            // Configurar el JSON mapper con Gson
            config.jsonMapper(new JsonMapper() {
                @NotNull
                @Override
                public String toJsonString(@NotNull Object obj, @NotNull Type type) {
                    return gson.toJson(obj, type);
                }

                @NotNull
                @Override
                public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                    return gson.fromJson(json, targetType);
                }
            });
        }).start(port);

        app.get("/status", controller::handleStatus);
        app.get("/search", controller::handleSearch);
        app.post("/search/refresh", controller::handleRefresh);
        app.get("/search/stats", controller::handleStats);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down Search Service...");
            app.stop();
        }));

        logger.info("Search Service started successfully on port {}", port);
    }

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    private static int getEnvInt(String key, int defaultValue) {
        String value = System.getenv(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
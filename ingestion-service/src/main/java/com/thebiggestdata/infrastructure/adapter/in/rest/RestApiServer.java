package com.thebiggestdata.infrastructure.adapter.in.rest;

import com.thebiggestdata.application.service.BookIngestionApplicationService;
import com.thebiggestdata.domain.port.out.BookStoragePort;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thebiggestdata.infrastructure.adapter.in.rest.controller.IngestionController;
import com.thebiggestdata.infrastructure.adapter.in.rest.dto.ErrorResponse;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Type;
import java.util.logging.Logger;


public class RestApiServer {
    private static final Logger logger = Logger.getLogger(RestApiServer.class.getName());
    private static final int DEFAULT_PORT = 7000;
    private final Javalin app;
    private final int port;
    private final IngestionController ingestionController;

    public RestApiServer(BookIngestionApplicationService bookIngestionApplicationService,
                        BookStoragePort bookStoragePort, int port) {
        this.port = port;
        this.ingestionController = new IngestionController(bookIngestionApplicationService, bookStoragePort);
        this.app = createApp();
    }

    public RestApiServer(
            BookIngestionApplicationService bookIngestionApplicationService,
            BookStoragePort bookStoragePort) {
        this(bookIngestionApplicationService, bookStoragePort, DEFAULT_PORT);
    }

    private Javalin createApp() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Javalin javalin = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.http.defaultContentType = "application/json";
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
        });
        configureRoutes(javalin);
        configureExceptionHandlers(javalin);
        return javalin;
    }

    private void configureRoutes(Javalin app) {
        app.post("/ingest/{book_id}", ingestionController::ingest);
        app.get("/ingest/status/{book_id}", ingestionController::status);
        app.get("/ingest/list", ingestionController::list);
        app.get("/", ctx -> ctx.result("Book Ingestion Service - Ingestion API"));
    }

    private void configureExceptionHandlers(Javalin app) {
        app.error(HttpStatus.NOT_FOUND, ctx -> {
            ctx.json(new ErrorResponse(
                    404,
                    "Not Found",
                    "The requested resource was not found",
                    ctx.path()
            ));
        });
        app.error(HttpStatus.BAD_REQUEST, ctx -> {
            ctx.json(new ErrorResponse(
                    400,
                    "Bad Request",
                    "Invalid request format or parameters",
                    ctx.path()
            ));
        });
        app.exception(Exception.class, (e, ctx) -> {
            logger.warning("Unhandled exception: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(new ErrorResponse(
                    500,
                    "Internal Server Error",
                    e.getMessage(),
                    ctx.path()
            ));
        });
    }

    public void start() {
        app.start(port);
        logger.info("REST API Server started on port " + port);
        logger.info("API documentation:");
        logger.info("  POST http://localhost:" + port + "/ingest/{book_id} - Download and ingest a book");
        logger.info("  GET http://localhost:" + port + "/ingest/status/{book_id} - Check book status");
        logger.info("  GET http://localhost:" + port + "/ingest/list - List all downloaded books");
    }

    public void stop() {
        app.stop();
        logger.info("REST API Server stopped");
    }

    public Javalin getApp() {
        return app;
    }
}

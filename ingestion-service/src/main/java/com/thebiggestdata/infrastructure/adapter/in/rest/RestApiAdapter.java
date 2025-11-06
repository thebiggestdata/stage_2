package com.thebiggestdata.infrastructure.adapter.in.rest;

import com.thebiggestdata.application.service.BookIngestionApplicationService;
import com.thebiggestdata.application.usecase.BookIngestionUseCase;
import com.thebiggestdata.domain.port.out.BookFetcherPort;
import com.thebiggestdata.domain.port.out.BookStoragePort;
import com.thebiggestdata.domain.port.out.FileSystemPort;
import com.thebiggestdata.domain.service.BookIngestionService;
import com.thebiggestdata.infrastructure.adapter.out.external.HttpBookFetcher;
import com.thebiggestdata.infrastructure.adapter.out.persistence.BookStorer;
import com.thebiggestdata.infrastructure.adapter.out.persistence.FileSystemBookRepository;
import com.thebiggestdata.infrastructure.util.DatalakePathBuilder;


public class RestApiAdapter {
    private static final String DEFAULT_DATALAKE_PATH = "datalake/";
    private static final int DEFAULT_PORT = 7000;

    public void run(String[] args) {
        String datalakePath = parseDatalakePath(args);
        int port = parsePort(args);
        BookStoragePort bookStoragePort = new FileSystemBookRepository(datalakePath);
        BookIngestionApplicationService bookIngestionService = createBookIngestionService(datalakePath, bookStoragePort);
        RestApiServer server = new RestApiServer(bookIngestionService, bookStoragePort, port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down REST API server...");
            server.stop();
        }));
        server.start();
    }

    private BookIngestionApplicationService createBookIngestionService(String datalakePath, BookStoragePort bookStoragePort) {
        BookFetcherPort bookFetcherPort = new HttpBookFetcher();
        DatalakePathBuilder pathBuilder = new DatalakePathBuilder(datalakePath);
        FileSystemPort fileSystemPort = new BookStorer(pathBuilder);
        BookIngestionService bookIngestionService = new BookIngestionService(
                bookFetcherPort,
                fileSystemPort,
                bookStoragePort
        );
        BookIngestionUseCase bookIngestionUseCase = new BookIngestionUseCase(bookIngestionService);
        return new BookIngestionApplicationService(bookIngestionUseCase);
    }

    private String parseDatalakePath(String[] args) {
        return args.length > 0 ? args[0] : DEFAULT_DATALAKE_PATH;
    }

    private int parsePort(String[] args) {
        if (args.length > 1) {
            try {return Integer.parseInt(args[1]);}
            catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default: " + DEFAULT_PORT);
                return DEFAULT_PORT;
            }
        }
        return DEFAULT_PORT;
    }
}

package com.thebiggestdata.infrastructure.adapter.in.rest.controller;

import com.thebiggestdata.application.dto.BookIngestionCommand;
import com.thebiggestdata.application.dto.BookIngestionResult;
import com.thebiggestdata.application.service.BookIngestionApplicationService;
import com.thebiggestdata.domain.port.out.BookStoragePort;
import com.thebiggestdata.infrastructure.adapter.in.rest.dto.ErrorResponse;
import com.thebiggestdata.infrastructure.adapter.in.rest.dto.IngestResponse;
import com.thebiggestdata.infrastructure.adapter.in.rest.dto.ListResponse;
import com.thebiggestdata.infrastructure.adapter.in.rest.dto.StatusResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.logging.Logger;


public class IngestionController {
    private static final Logger logger = Logger.getLogger(IngestionController.class.getName());
    private final BookIngestionApplicationService applicationService;
    private final BookStoragePort bookStoragePort;

    public IngestionController(BookIngestionApplicationService applicationService, BookStoragePort bookStoragePort) {
        this.applicationService = applicationService;
        this.bookStoragePort = bookStoragePort;
    }

    public void ingest(Context ctx) {
        try {
            int bookId = ctx.pathParamAsClass("book_id", Integer.class).get();
            BookIngestionCommand command = new BookIngestionCommand(bookId);
            BookIngestionResult result = applicationService.ingestBook(command);

            String status = result.success() ? "downloaded" : "failed";
            String path = result.success() ? extractPath(result.bodyPath()) : null;

            IngestResponse response = new IngestResponse(bookId, status, path);
            ctx.status(HttpStatus.OK).json(response);
        } catch (Exception e) {
            logger.warning("Error ingesting book: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(
                    new ErrorResponse(500, "Internal Server Error", e.getMessage(), ctx.path())
            );
        }
    }

    public void status(Context ctx) {
        try {
            int bookId = ctx.pathParamAsClass("book_id", Integer.class).get();
            boolean exists = bookStoragePort.exists(bookId);
            String status = exists ? "available" : "not_found";

            StatusResponse response = new StatusResponse(bookId, status);
            ctx.status(HttpStatus.OK).json(response);
        } catch (Exception e) {
            logger.warning("Error checking book status: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(
                    new ErrorResponse(500, "Internal Server Error", e.getMessage(), ctx.path())
            );
        }
    }

    public void list(Context ctx) {
        try {
            List<Integer> bookIds = bookStoragePort.findAllBookIds();
            ListResponse response = new ListResponse(bookIds.size(), bookIds);
            ctx.status(HttpStatus.OK).json(response);
        } catch (Exception e) {
            logger.warning("Error listing books: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(
                    new ErrorResponse(500, "Internal Server Error", e.getMessage(), ctx.path())
            );
        }
    }

    private String extractPath(String bodyPath) {
        if (bodyPath == null) return null;
        // Extract path up to book ID (e.g., "datalake/20251008/14/1342.body.txt" -> "datalake/20251008/14/1342")
        int lastDot = bodyPath.lastIndexOf('.');
        int secondLastDot = bodyPath.lastIndexOf('.', lastDot - 1);
        return bodyPath.substring(0, secondLastDot);
    }
}


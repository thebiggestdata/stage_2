package com.thebiggestdata.infrastructure.adapter.in.rest.controller;

import com.thebiggestdata.application.dto.*;
import com.thebiggestdata.application.service.BookIngestionApplicationService;
import com.thebiggestdata.infrastructure.adapter.in.rest.dto.BookIngestionRequest;
import com.thebiggestdata.infrastructure.adapter.in.rest.dto.ErrorResponse;
import com.thebiggestdata.infrastructure.adapter.in.rest.mapper.RestDtoMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.logging.Logger;


public class BookIngestionController {
    private static final Logger logger = Logger.getLogger(BookIngestionController.class.getName());
    private final BookIngestionApplicationService applicationService;

    public BookIngestionController(BookIngestionApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public void ingestBook(Context ctx) {
        try {
            BookIngestionRequest request = ctx.bodyAsClass(BookIngestionRequest.class);
            BookIngestionCommand command = RestDtoMapper.toCommand(request);
            BookIngestionResult result = applicationService.ingestBook(command);
            var response = RestDtoMapper.toRestDto(result);
            if (result.success()) ctx.status(HttpStatus.CREATED).json(response);
            else ctx.status(HttpStatus.OK).json(response);
        } catch (Exception e) {
            logger.warning("Error ingesting book: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(
                    new ErrorResponse(500, "Internal Server Error", e.getMessage(), ctx.path())
            );
        }
    }

    public void ingestBookByPathParam(Context ctx) {
        try {
            int bookId = ctx.pathParamAsClass("bookId", Integer.class).get();
            BookIngestionCommand command = new BookIngestionCommand(bookId);
            BookIngestionResult result = applicationService.ingestBook(command);
            var response = RestDtoMapper.toRestDto(result);
            if (result.success()) ctx.status(HttpStatus.CREATED).json(response);
            else ctx.status(HttpStatus.OK).json(response);
        } catch (Exception e) {
            logger.warning("Error ingesting book: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(
                    new ErrorResponse(500, "Internal Server Error", e.getMessage(), ctx.path())
            );
        }
    }
}
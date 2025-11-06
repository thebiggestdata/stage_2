package com.thebiggestdata.infrastructure.adapter.in.rest.controller;

import com.thebiggestdata.application.dto.*;
import com.thebiggestdata.application.service.BookIngestionApplicationService;
import com.thebiggestdata.infrastructure.adapter.in.rest.dto.BulkIngestionRequest;
import com.thebiggestdata.infrastructure.adapter.in.rest.dto.ErrorResponse;
import com.thebiggestdata.infrastructure.adapter.in.rest.mapper.RestDtoMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.logging.Logger;


public class BulkIngestionController {
    private static final Logger logger = Logger.getLogger(BulkIngestionController.class.getName());
    private final BookIngestionApplicationService applicationService;

    public BulkIngestionController(BookIngestionApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public void bulkIngest(Context ctx) {
        try {
            BulkIngestionRequest request = ctx.bodyAsClass(BulkIngestionRequest.class);
            BulkIngestionCommand command = RestDtoMapper.toCommand(request);
            BulkIngestionResult result = applicationService.bulkIngest(command);
            var response = RestDtoMapper.toRestDto(result);
            ctx.status(HttpStatus.OK).json(response);
        } catch (Exception e) {
            logger.warning("Error in bulk ingestion: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(
                    new ErrorResponse(500, "Internal Server Error", e.getMessage(), ctx.path())
            );
        }
    }
}

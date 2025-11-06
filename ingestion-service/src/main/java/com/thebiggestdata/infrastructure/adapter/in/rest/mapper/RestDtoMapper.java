package com.thebiggestdata.infrastructure.adapter.in.rest.mapper;

import com.thebiggestdata.application.dto.*;
import com.thebiggestdata.infrastructure.adapter.in.rest.dto.*;


public class RestDtoMapper {

    public static BookIngestionCommand toCommand(BookIngestionRequest dto) {
        return new BookIngestionCommand(dto.bookId());
    }

    public static BulkIngestionCommand toCommand(BulkIngestionRequest dto) {
        return new BulkIngestionCommand(
                dto.startId(),
                dto.endId(),
                dto.delayMs()
        );
    }

    public static BookIngestionResponse toRestDto(BookIngestionResult result) {
        return new BookIngestionResponse(
                result.bookId(),
                result.success(),
                result.message(),
                result.headerPath(),
                result.bodyPath(),
                result.timestamp()
        );
    }

    public static BulkIngestionResponse toRestDto(BulkIngestionResult result) {
        return new BulkIngestionResponse(
                result.startId(),
                result.endId(),
                result.totalRequested(),
                result.successfulCount(),
                result.failedCount(),
                result.status(),
                result.message()
        );
    }

    public static HealthResponse toRestDto(HealthStatus status) {
        return new HealthResponse(
                status.status(),
                status.service(),
                status.version(),
                status.timestamp()
        );
    }
}

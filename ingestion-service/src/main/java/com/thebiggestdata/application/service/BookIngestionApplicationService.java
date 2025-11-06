package com.thebiggestdata.application.service;

import com.thebiggestdata.application.dto.*;
import com.thebiggestdata.application.usecase.BookIngestionUseCase;
import com.thebiggestdata.domain.model.StorageResult;
import java.util.logging.Logger;


public class BookIngestionApplicationService {
    private static final Logger logger = Logger.getLogger(BookIngestionApplicationService.class.getName());
    private final BookIngestionUseCase bookIngestionUseCase;

    public BookIngestionApplicationService(BookIngestionUseCase bookIngestionUseCase) {
        this.bookIngestionUseCase = bookIngestionUseCase;
    }

    public BookIngestionResult ingestBook(BookIngestionCommand command) {
        int bookId = command.bookId();
        logger.info("Application Service: Ingesting book " + bookId);
        StorageResult result = bookIngestionUseCase.ingestBook(bookId);
        return new BookIngestionResult(
                bookId,
                result.success(),
                result.success() ? "Book ingested successfully" : "Book already exists or failed to ingest",
                result.headerPath(),
                result.bodyPath(),
                result.timestamp()
        );
    }

    public BulkIngestionResult bulkIngest(BulkIngestionCommand command) {
        int startId = command.startId();
        int endId = command.endId();
        long delay = command.delayMs();
        logger.info(String.format("Application Service: Bulk ingesting books from %d to %d", startId, endId));
        int total = endId - startId + 1;
        int successful = 0;
        int failed = 0;
        for (int bookId = startId; bookId <= endId; bookId++) {
            StorageResult result = bookIngestionUseCase.ingestBook(bookId);
            if (result.success()) successful++;
            else failed++;
            if (bookId < endId) sleep(delay);
        }
        return new BulkIngestionResult(
                startId,
                endId,
                total,
                successful,
                failed,
                "completed",
                String.format("Bulk ingestion completed: %d/%d successful", successful, total)
        );
    }

    private void sleep(long millis) {
        try {Thread.sleep(millis);}
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Sleep interrupted: " + e.getMessage());
        }
    }
}


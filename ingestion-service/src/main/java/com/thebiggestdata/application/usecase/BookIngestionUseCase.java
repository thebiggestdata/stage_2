package com.thebiggestdata.application.usecase;

import com.thebiggestdata.domain.model.Book;
import com.thebiggestdata.domain.model.StorageResult;
import com.thebiggestdata.domain.service.BookIngestionService;
import java.util.logging.Logger;


public class BookIngestionUseCase {
    private static final Logger logger = Logger.getLogger(BookIngestionUseCase.class.getName());

    private final BookIngestionService bookIngestionService;

    public BookIngestionUseCase(BookIngestionService bookIngestionService) {
        this.bookIngestionService = bookIngestionService;
    }

    public StorageResult ingestBook(int bookId) {
        if (bookIngestionService.bookExists(bookId)) {
            logger.info("Book " + bookId + " already exists, skipping ingestion");
            return new StorageResult(false, null, null, null);
        }
        try {
            Book book = bookIngestionService.fetchBook(bookId);
            StorageResult result = bookIngestionService.storeBook(book);
            if (result.success()) logger.info("Successfully ingested book: " + bookId);
            return result;
        } catch (Exception e) {
            logger.warning("Failed to ingest book " + bookId + ": " + e.getMessage());
            return new StorageResult(false, null, null, null);
        }
    }
}

package com.thebiggestdata.infrastructure.adapter.in;

import com.thebiggestdata.application.config.CrawlerConfig;
import com.thebiggestdata.application.usecase.BookIngestionUseCase;
import com.thebiggestdata.domain.model.StorageResult;
import java.util.logging.Logger;


public class CrawlerController {
    private static final Logger logger = Logger.getLogger(CrawlerController.class.getName());
    private final CrawlerConfig config;
    private final BookIngestionUseCase bookIngestionUseCase;

    public CrawlerController(CrawlerConfig config, BookIngestionUseCase bookIngestionUseCase) {
        this.config = config;
        this.bookIngestionUseCase = bookIngestionUseCase;
    }

    public StorageResult downloadBook(int bookId) {
        logger.info("Processing book " + bookId);
        return bookIngestionUseCase.ingestBook(bookId);
    }

    public void crawlRange() {crawlRange(config.startId(), config.endId());}

    public void crawlRange(int startId, int endId) {
        int total = endId - startId + 1;
        int successful = 0;
        logger.info(String.format("Starting crawl from book %d to %d", startId, endId));
        for (int bookId = startId; bookId <= endId; bookId++) {
            StorageResult result = downloadBook(bookId);
            if (result.success()) successful++;
            waitBetweenDownloads();
        }
        logger.info(String.format("Crawling completed: %d/%d books successfully downloaded", successful, total));
    }

    private void waitBetweenDownloads() {
        try {Thread.sleep(config.delay());}
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Crawler interrupted: " + e.getMessage());
        }
    }
}
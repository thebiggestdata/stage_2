package com.thebiggestdata.ingestion.control;

import com.thebiggestdata.ingestion.control.utils.BookStorageRepository;
import com.thebiggestdata.ingestion.control.utils.FileSystemBookRepository;
import com.thebiggestdata.ingestion.control.fetch.BookFetcher;
import com.thebiggestdata.ingestion.control.serializer.BookSerializer;
import com.thebiggestdata.ingestion.control.storer.BookStorer;
import com.thebiggestdata.ingestion.control.utils.CrawlerConfig;
import com.thebiggestdata.ingestion.control.utils.DatalakePathBuilder;
import com.thebiggestdata.ingestion.model.Book;
import com.thebiggestdata.ingestion.model.StorageResult;
import java.util.logging.Logger;

public class CrawlerController {
    private static final Logger logger = Logger.getLogger(CrawlerController.class.getName());
    private final CrawlerConfig config;
    private final BookFetcher fetcher;
    private final BookStorer storage;
    private final BookStorageRepository storageRepository;
    private int currentId;

    public CrawlerController(CrawlerConfig config, String datalakeBasePath) {
        this.config = config;
        this.currentId = config.startId();
        this.fetcher = new BookFetcher();
        DatalakePathBuilder pathBuilder = new DatalakePathBuilder(datalakeBasePath);
        this.storage = new BookStorer(pathBuilder);
        this.storageRepository = new FileSystemBookRepository(datalakeBasePath);
    }

    public StorageResult downloadBook(int bookId) {
        if (storageRepository.exists(bookId)) {
            logger.info("Book " + bookId + " already downloaded, skipping");
            return new StorageResult(false, null, null, null);
        }
        try {
            logger.info("Downloading book " + bookId);
            String content = fetcher.fetch(bookId);
            BookSerializer serializer = new BookSerializer(content, bookId);
            Book book = serializer.serialize();
            StorageResult result = storage.save(book);
            if (result.success()) logger.info("Successfully downloaded book " + bookId);
            return result;
        } catch (Exception e) {
            logger.warning("Failed to download book " + bookId + ": " + e.getMessage());
            return new StorageResult(false, null, null, null);
        }
    }

    public void crawlRange() {crawlRange(config.startId(), config.endId());}

    public void crawlRange(int startId, int endId) {
        int total = endId - startId + 1;
        int successful = 0;
        logger.info(String.format("Starting crawl from book %d to %d", startId, endId));
        for (int bookId = startId; bookId <= endId; bookId++) {
            logger.info("Processing book " + bookId);
            StorageResult result = downloadBook(bookId);
            if (result.success()) successful++;
            waitBetweenDownloads();
        }
        logger.info(String.format("Downloaded %d/%d books", successful, total));
    }

    private void waitBetweenDownloads() {
        try {Thread.sleep(config.delay());}
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Crawler interrupted: " + e.getMessage());
        }
    }

    public int getCurrentId() {return currentId;}
}
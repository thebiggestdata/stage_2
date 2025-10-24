package control;

import control.fetch.BookFetcher;
import control.serializer.BookSerializer;
import control.storer.BookStorer;
import control.utils.CrawlerConfig;
import control.utils.DatalakePathBuilder;
import model.Book;
import model.StorageResult;
import java.util.logging.Logger;

public class CrawlerController {
    private static final Logger logger = Logger.getLogger(CrawlerController.class.getName());
    private final CrawlerConfig config;
    private final BookFetcher fetcher;
    private final BookStorer storage;
    private int currentId;

    public CrawlerController(CrawlerConfig config) {
        this.config = config;
        this.currentId = config.startId();
        this.fetcher = new BookFetcher();
        DatalakePathBuilder pathBuilder = new DatalakePathBuilder();
        this.storage = new BookStorer(pathBuilder);
    }

    public StorageResult downloadBook(int bookId) {
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

    public StorageResult downloadNextBook() {
        int bookId = currentId;
        StorageResult result = downloadBook(bookId);
        currentId++;
        return result;
    }

    public void setCurrentId(int bookId) {this.currentId = bookId;}

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
        logger.info(String.format("Downloaded %d/%d books successfully", successful, total));
    }

    private void waitBetweenDownloads() {
        try {
            Thread.sleep(config.delay());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Crawler interrupted: " + e.getMessage());
        }
    }

    public int getCurrentId() {return currentId;}
}

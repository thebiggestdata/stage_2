package control;

import control.fetch.BookFetcher;
import control.serializer.BookSerializer;
import control.storer.BookStorer;
import control.utils.CrawlerConfig;
import control.utils.DatalakePathBuilder;
import model.Book;

import java.util.logging.Logger;

public class CrawlerController {
    private static final Logger logger = Logger.getLogger(CrawlerController.class.getName());
    private final CrawlerConfig config;
    private final BookFetcher fetcher;
    private final BookStorer storer;
    private final DatalakePathBuilder pathBuilder;

    public CrawlerController(CrawlerConfig config) {
        this.config = config;
        this.fetcher = new BookFetcher();
        this.pathBuilder = new DatalakePathBuilder();
        this.storer = new BookStorer();
    }

    public void crawlRange() {
        for (int bookId = config.startId(); bookId <= config.endId(); bookId++) {
            processBook(bookId);
            waitBetweenDownloads();
        }
    }

    private void processBook(int bookId) {
        logger.info("Processing book " + bookId);
        try {downloadAndSaveBook(bookId);}
        catch (Exception e) {logger.warning("Failed to process book " + bookId + ": " + e.getMessage());}
    }

    private void downloadAndSaveBook(int bookId) throws Exception {
        String content = fetcher.fetch(bookId);
        Book book = new BookSerializer(content, bookId).serialize();
        String directory = pathBuilder.buildPath();
        storer.save(book, directory);
    }

    private void waitBetweenDownloads() {
        try {
            Thread.sleep(config.delay());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Crawler interrupted", e);
        }
    }
}

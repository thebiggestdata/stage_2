package api.service;

import control.CrawlerController;
import control.utils.CrawlerConfig;
import model.StorageResult;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class IngestionService {
    private static final Logger logger = Logger.getLogger(IngestionService.class.getName());

    private CrawlerController crawler;
    private CrawlerConfig currentConfig;

    public IngestionService() {
        this.currentConfig = new CrawlerConfig(1, 1000, 1000L);
        this.crawler = new CrawlerController(currentConfig);
    }

    public StorageResult downloadBook(int bookId) {
        logger.info("Service: Downloading book " + bookId);
        return crawler.downloadBook(bookId);
    }

    public StorageResult downloadNextBook() {
        logger.info("Service: Downloading next book");
        return crawler.downloadNextBook();
    }

    public void crawlRange(int startId, int endId) {
        logger.info(String.format("Service: Crawling range %d to %d", startId, endId));
        crawler.crawlRange(startId, endId);
    }

    public void updateConfiguration(CrawlerConfig newConfig) {
        logger.info("Service: Updating crawler configuration");
        this.currentConfig = newConfig;
        this.crawler = new CrawlerController(newConfig);
    }

    public int getCurrentBookId() {
        return crawler.getCurrentId();
    }

    public CrawlerConfig getCurrentConfig() {
        return currentConfig;
    }
}


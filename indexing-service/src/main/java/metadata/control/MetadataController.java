package metadata.control;

import metadata.control.extractor.MetadataExtractor;
import metadata.control.parser.HeaderParser;
import metadata.control.storage.DbStorageInterface;
import metadata.control.storage.MongoDbStorage;
import metadata.model.BookMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

public class MetadataController {
    private final Logger logger = LoggerFactory.getLogger(MetadataController.class);
    private final DbStorageInterface storage;
    private final MetadataExtractor extractor;

    public MetadataController(DbStorageInterface storage, HeaderParser headerParser, String datalakeBasePath) {
        this.storage = storage;
        this.extractor = new MetadataExtractor(storage, headerParser, datalakeBasePath);
        logger.info("MetadataController initialized with datalake : {}", datalakeBasePath);
    }

    public boolean initialize() {
        boolean success = storage.initialize();
        if (success) logger.info("MetadataController storage initialized successfully");
        else logger.error("Failed to initialize MetadataController storage");
        return success;
    }

    public boolean extractAndStoreMetadata(int bookId, String downloadDate, String downloadHour) {
        logger.info("Starting metadata extraction for book {}", bookId);
        return extractor.extractAndStoreMetadata(bookId, downloadDate, downloadHour);
    }

    public boolean processBooks(List<Integer> bookIds, String downloadDate, String downloadHour) {
        logger.info("Processing {} books for metadata extraction", bookIds.size());
        int successful = 0;
        int failed = 0;
        for (int bookId : bookIds) {
            if (extractor.extractAndStoreMetadata(bookId, downloadDate, downloadHour)) successful++;
            else failed++;
        }
        logger.info("Batch processing completed: {} successful, {} failed", successful, failed);
        return failed == 0;
    }

    public BookMetadata getBookById(int bookId) {
        logger.debug("Retrieving book with ID: {}", bookId);
        return storage.getBookById(bookId);
    }

    public List<BookMetadata> getBooksByAuthor(String author) {
        logger.debug("Retrieving books by author: {}", author);
        return storage.getBooksByAuthor(author);
    }

    public List<BookMetadata> getAllBooks(int limit) {
        logger.debug("Retrieving all books with limit: {}", limit);
        return storage.getAllBooks(limit);
    }

    public int getTotalBooks() {
        int total = storage.getTotalBooks();
        logger.debug("Total books in storage: {}", total);
        return total;
    }

    public boolean bookExists(int bookId) {
        return storage.bookExists(bookId);
    }

    public Map<String, Object> getStatistics() {
        logger.debug("Retrieving storage statistics");
        return extractor.getStorageStatistics();
    }

    public void shutdown() {
        logger.info("Shutting down MetadataController");
        storage.close();
    }
}

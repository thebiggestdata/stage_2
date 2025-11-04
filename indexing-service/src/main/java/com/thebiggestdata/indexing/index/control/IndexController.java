package com.thebiggestdata.indexing.index.control;

import com.thebiggestdata.indexing.index.control.mongoDb.DbInterface;
import com.thebiggestdata.indexing.index.control.mongoDb.MongoInvertedIndex;
import com.thebiggestdata.indexing.index.model.Processor;
import com.thebiggestdata.indexing.index.model.StopWordFilter;
import com.thebiggestdata.indexing.index.model.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndexController {
    private final Logger logger = LoggerFactory.getLogger(IndexController.class);
    private final DbInterface invertedIndex;
    private final BookIndexer bookIndexer;
    private final Processor textProcessor;

    public IndexController(DbInterface invertedIndex, Processor textProcessor, String datalakeBasePath) {
        this.invertedIndex = invertedIndex;
        this.textProcessor = textProcessor;
        this.bookIndexer = new BookIndexer(invertedIndex, textProcessor, datalakeBasePath);
        logger.info("IndexController initialized");
    }

    public IndexController() {
        this(new MongoInvertedIndex(),
                new Processor(new Tokenizer(), new StopWordFilter()),
                "datalake/"
        );
    }

    public IndexController(String language, String datalakeBasePath) {
        this(new MongoInvertedIndex(),
                new Processor(new Tokenizer(), new StopWordFilter(language)),
                datalakeBasePath
        );
    }

    public boolean initialize(String uri, String dbName) {
        boolean success = invertedIndex.connect(uri, dbName);
        if (success) logger.info("IndexController inverted index initialized successfully");
        else logger.error("Failed to initialize IndexController inverted index");
        return success;
    }

    public boolean initialize() {
        return initialize("mongodb://localhost:27017/", "inverted_index");
    }

    public boolean indexBook(int bookId, String downloadDate, String downloadHour) {
        logger.info("Starting indexing for book {}", bookId);
        return bookIndexer.indexBook(bookId, downloadDate, downloadHour);
    }

    public boolean indexBooks(List<Integer> bookIds, String downloadDate, String downloadHour) {
        logger.info("Processing {} books for indexing", bookIds.size());
        int successful = 0;
        int failed = 0;
        for (int bookId : bookIds) {
            if (bookIndexer.indexBook(bookId, downloadDate, downloadHour)) successful++;
            else failed++;
        }
        logger.info(
                "Batch indexing completed: {} successful, {} failed",
                successful, failed
        );
        return failed == 0;
    }

    public Set<Integer> searchByTerm(String term) {
        logger.debug("Searching documents for term: {}", term);
        String normalizedTerm = term.toLowerCase();
        return invertedIndex.seekDocumentsByTerm(normalizedTerm);
    }

    public List<String> getAllIndexedTerms() {
        logger.debug("Retrieving all indexed terms");
        return invertedIndex.getAllTerms();
    }

    public int getIndexSize() {
        int size = invertedIndex.getIndexSize();
        logger.debug("Index size: {} unique terms", size);
        return size;
    }

    public Map<String, Object> getIndexStatistics() {
        logger.debug("Retrieving index statistics");
        return bookIndexer.getIndexStatistics();
    }

    public List<String> processText(String text) {
        logger.debug("Processing text of length: {}", text.length());
        return textProcessor.process(text);
    }

    public void shutdown() {
        logger.info("Shutting down IndexController");
        invertedIndex.close();
    }
}


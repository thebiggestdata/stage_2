package com.thebiggestdata.indexing.api.service;

import com.thebiggestdata.indexing.api.dto.IndexStatusDto;
import com.thebiggestdata.indexing.api.dto.IndexUpdateResultDto;
import com.thebiggestdata.indexing.api.dto.RebuildResultDto;
import com.thebiggestdata.indexing.index.control.IndexController;
import com.thebiggestdata.indexing.metadata.control.MetadataController;
import com.thebiggestdata.indexing.metadata.control.parser.HeaderParser;
import com.thebiggestdata.indexing.metadata.control.storage.MongoDbStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class IndexingService {
    private static final Logger logger = LoggerFactory.getLogger(IndexingService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH");

    private final IndexController indexController;
    private final MetadataController metadataController;
    private final String datalakeBasePath;

    public IndexingService(
            @Value("${datalake.base-path:datalake/}") String datalakeBasePath,
            @Value("${mongodb.connection-string:mongodb://localhost:27017/}") String mongoUri,
            @Value("${mongodb.inverted-index.database:inverted_index}") String indexDb,
            @Value("${mongodb.metadata.database:metadata}") String metadataDb) {

        this.datalakeBasePath = datalakeBasePath;
        this.indexController = new IndexController("en", datalakeBasePath);
        this.metadataController = new MetadataController(
                new MongoDbStorage(),
                new HeaderParser(),
                datalakeBasePath
        );

        // Initialize both controllers
        boolean indexInit = indexController.initialize(mongoUri, indexDb);
        boolean metadataInit = metadataController.initialize();

        if (!indexInit || !metadataInit) {
            throw new RuntimeException("Failed to initialize indexing or metadata controllers");
        }

        logger.info("IndexingService initialized successfully");
    }

    public IndexUpdateResultDto indexBook(int bookId, String downloadDate, String downloadHour) {
        logger.info("Indexing book {} from {}/{}", bookId, downloadDate, downloadHour);

        try {
            // Step 1: Extract and store metadata
            boolean metadataSuccess = metadataController.extractAndStoreMetadata(
                    bookId, downloadDate, downloadHour
            );

            if (!metadataSuccess) {
                logger.warn("Metadata extraction failed for book {}", bookId);
                return new IndexUpdateResultDto(
                        bookId,
                        false,
                        "metadata_failed",
                        "Failed to extract metadata"
                );
            }

            // Step 2: Index the book content
            boolean indexSuccess = indexController.indexBook(bookId, downloadDate, downloadHour);

            if (!indexSuccess) {
                logger.warn("Indexing failed for book {}", bookId);
                return new IndexUpdateResultDto(
                        bookId,
                        false,
                        "indexing_failed",
                        "Failed to update inverted index"
                );
            }

            logger.info("Successfully indexed book {}", bookId);
            return new IndexUpdateResultDto(
                    bookId,
                    true,
                    "indexed",
                    "Book successfully indexed"
            );

        } catch (Exception e) {
            logger.error("Error indexing book {}: {}", bookId, e.getMessage(), e);
            return new IndexUpdateResultDto(
                    bookId,
                    false,
                    "error",
                    e.getMessage()
            );
        }
    }

    public RebuildResultDto rebuildIndex() {
        logger.info("Starting full index rebuild");
        long startTime = System.currentTimeMillis();

        try {
            List<BookInfo> allBooks = scanDatalake();
            logger.info("Found {} books in datalake", allBooks.size());

            int successful = 0;
            int failed = 0;

            for (BookInfo bookInfo : allBooks) {
                IndexUpdateResultDto result = indexBook(
                        bookInfo.bookId,
                        bookInfo.downloadDate,
                        bookInfo.downloadHour
                );

                if (result.success()) {
                    successful++;
                } else {
                    failed++;
                }
            }

            long elapsedMs = System.currentTimeMillis() - startTime;
            String elapsedTime = String.format("%.1fs", elapsedMs / 1000.0);

            logger.info("Rebuild completed: {} successful, {} failed", successful, failed);

            return new RebuildResultDto(
                    successful,
                    failed,
                    elapsedTime,
                    "Rebuild completed"
            );

        } catch (Exception e) {
            logger.error("Error during rebuild: {}", e.getMessage(), e);
            long elapsedMs = System.currentTimeMillis() - startTime;
            String elapsedTime = String.format("%.1fs", elapsedMs / 1000.0);

            return new RebuildResultDto(
                    0,
                    0,
                    elapsedTime,
                    "Rebuild failed: " + e.getMessage()
            );
        }
    }

    public IndexStatusDto getIndexStatus() {
        logger.debug("Retrieving index status");

        try {
            Map<String, Object> indexStats = indexController.getIndexStatistics();
            Map<String, Object> metadataStats = metadataController.getStatistics();

            int booksIndexed = metadataController.getTotalBooks();
            int uniqueTerms = indexController.getIndexSize();

            String lastUpdate = Instant.now()
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            // Calculate approximate index size (very rough estimate)
            double indexSizeMB = uniqueTerms * 0.001; // Rough estimate

            return new IndexStatusDto(
                    booksIndexed,
                    uniqueTerms,
                    lastUpdate,
                    indexSizeMB,
                    indexStats.get("index_type").toString(),
                    metadataStats.get("storage_type").toString()
            );

        } catch (Exception e) {
            logger.error("Error retrieving status: {}", e.getMessage(), e);
            return new IndexStatusDto(
                    0,
                    0,
                    Instant.now().toString(),
                    0.0,
                    "unknown",
                    "unknown"
            );
        }
    }

    private List<BookInfo> scanDatalake() throws IOException {
        List<BookInfo> books = new ArrayList<>();
        Path datalakePath = Paths.get(datalakeBasePath);

        if (!Files.exists(datalakePath)) {
            logger.warn("Datalake path does not exist: {}", datalakePath);
            return books;
        }

        // Scan datalake structure: datalake/YYYYMMDD/HH/*.body.txt
        try (Stream<Path> dateDirs = Files.list(datalakePath)) {
            dateDirs.filter(Files::isDirectory).forEach(dateDir -> {
                String date = dateDir.getFileName().toString();

                try (Stream<Path> hourDirs = Files.list(dateDir)) {
                    hourDirs.filter(Files::isDirectory).forEach(hourDir -> {
                        String hour = hourDir.getFileName().toString();

                        try (Stream<Path> files = Files.list(hourDir)) {
                            files.filter(f -> f.getFileName().toString().endsWith(".body.txt"))
                                    .forEach(file -> {
                                        String filename = file.getFileName().toString();
                                        int bookId = Integer.parseInt(
                                                filename.replace(".body.txt", "")
                                        );
                                        books.add(new BookInfo(bookId, date, hour));
                                    });
                        } catch (IOException e) {
                            logger.error("Error scanning hour directory {}: {}", hourDir, e.getMessage());
                        }
                    });
                } catch (IOException e) {
                    logger.error("Error scanning date directory {}: {}", dateDir, e.getMessage());
                }
            });
        }

        return books;
    }

    private static class BookInfo {
        final int bookId;
        final String downloadDate;
        final String downloadHour;

        BookInfo(int bookId, String downloadDate, String downloadHour) {
            this.bookId = bookId;
            this.downloadDate = downloadDate;
            this.downloadHour = downloadHour;
        }
    }
}
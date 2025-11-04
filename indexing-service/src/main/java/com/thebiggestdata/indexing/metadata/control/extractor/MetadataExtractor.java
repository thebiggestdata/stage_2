package com.thebiggestdata.indexing.metadata.control.extractor;

import com.thebiggestdata.indexing.metadata.model.BookMetadata;
import com.thebiggestdata.indexing.metadata.control.parser.HeaderParser;
import com.thebiggestdata.indexing.metadata.control.storage.DbStorageInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MetadataExtractor {
    private final Logger logger = LoggerFactory.getLogger(MetadataExtractor.class);
    private final DbStorageInterface metadataStorage;
    private final HeaderParser headerParser;
    private final Path datalakeBasePath;

    public MetadataExtractor(DbStorageInterface metadataStorage, HeaderParser headerParser, String datalakeBasePath) {
        this.metadataStorage = metadataStorage;
        this.headerParser = headerParser != null ? headerParser : new HeaderParser();
        this.datalakeBasePath = Paths.get(datalakeBasePath);
        logger.info("MetadataExtractor initialized");
    }

    public MetadataExtractor(DbStorageInterface metadataStorage) {
        this(metadataStorage, null, "datalake");
    }

    public boolean extractAndStoreMetadata(int bookId, String downloadDate, String downloadHour) {
        try {
            String headerText = readBookHeader(bookId, downloadDate, downloadHour);
            if (headerText == null) {
                logger.error("Failed to read header for book {}, skipping metadata extraction", bookId);
                return false;
            }
            Map<String, String> parsedHeader = headerParser.parse(headerText, bookId);
            BookMetadata metadata = new BookMetadata(
                    bookId,
                    parsedHeader.get("title"),
                    parsedHeader.get("author"),
                    parsedHeader.get("language"),
                    parsedHeader.get("release_date")
            );
            if (!metadata.isComplete()) {
                logger.warn(
                        "Book {}: extracted incomplete metadata - some essential fields are missing",
                        bookId
                );
            }
            logger.info("Book {}: extracted metadata - Title: '{}', Author: '{}'", bookId, metadata.title(), metadata.author());
            boolean success = metadataStorage.insertBookMetadata(metadata);
            if (success) logger.info("Book {}: successfully stored metadata in database", bookId);
            else logger.error("Book {}: failed to store metadata in database", bookId);
            return success;
        } catch (Exception e) {
            logger.error("Unexpected error extracting metadata for book {}: {}", bookId, e.getMessage(), e);
            return false;
        }
    }

    private String readBookHeader(int bookId, String downloadDate, String downloadHour) {
        try {
            Path headerPath = datalakeBasePath
                    .resolve(downloadDate)
                    .resolve(downloadHour)
                    .resolve(bookId + ".header.txt");
            if (!Files.exists(headerPath)) {
                logger.error("Header file not found: {}", headerPath);
                return null;
            }
            String content = Files.readString(headerPath, StandardCharsets.UTF_8);
            if (content.trim().isEmpty()) {
                logger.warn("Header file is empty for book {}", bookId);
                return null;
            }
            logger.debug("Successfully read {} characters from header of book {}", content.length(), bookId);
            return content;
        } catch (IOException e) {
            logger.error("Failed to read header file for book {}: {}", bookId, e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error(
                    "Encoding error reading header for book {}. File may not be UTF-8: {}",
                    bookId, e.getMessage()
            );
            return null;
        }
    }

    public Map<String, Object> getStorageStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_books", metadataStorage.getTotalBooks());
        stats.put("storage_type", metadataStorage.getClass().getSimpleName());
        return stats;
    }
}

package api.service;

import api.dto.BookListDto;
import api.dto.BookStatusDto;
import api.dto.IngestionResultDto;
import control.CrawlerController;
import model.StorageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Service
public class IngestionService {
    private static final Logger logger = LoggerFactory.getLogger(IngestionService.class);
    private static final String DATALAKE_BASE_PATH = "datalake";
    private final CrawlerController crawlerController;
    private final BookStorageRepository storageRepository;

    public IngestionService(CrawlerController crawlerController, BookStorageRepository storageRepository) {
        this.crawlerController = crawlerController;
        this.storageRepository = storageRepository;
    }

    public IngestionResultDto ingestBook(int bookId) {
        logger.info("Ingesting book {}", bookId);
        try {
            if (storageRepository.exists(bookId)) {
                logger.info("Book {} already downloaded, skipping", bookId);
                return new IngestionResultDto(bookId, "already_downloaded", null);
            }
            StorageResult result = crawlerController.downloadBook(bookId);
            if (result == null || !result.success()) {
                logger.warn("Failed to ingest book {}", bookId);
                return new IngestionResultDto(bookId, "failed", null);
            }
            String basePath = extractBasePath(result.headerPath());
            logger.info("Book {} successfully ingested at {}", bookId, basePath);
            return new IngestionResultDto(bookId, "downloaded", basePath);
        } catch (Exception e) {
            logger.error("Error ingesting book {}: {}", bookId, e.getMessage(), e);
            return new IngestionResultDto(bookId, "failed", null);
        }
    }

    public BookStatusDto getBookStatus(int bookId) {
        logger.debug("Checking status for book {}", bookId);
        try {
            boolean exists = storageRepository.exists(bookId);
            String status = exists ? "available" : "not_available";
            return new BookStatusDto(bookId, status);
        } catch (Exception e) {
            logger.error("Error checking status for book {}: {}", bookId, e.getMessage(), e);
            return new BookStatusDto(bookId, "error");
        }
    }

    public BookListDto listDownloadedBooks() {
        logger.debug("Listing all downloaded books");
        try {
            List<Integer> bookIds = storageRepository.findAllBookIds();
            return new BookListDto(bookIds.size(), bookIds);
        } catch (Exception e) {
            logger.error("Error listing books: {}", e.getMessage(), e);
            return new BookListDto(0, Collections.emptyList());
        }
    }

    private String extractBasePath(String headerPath) {
        if (headerPath == null) return null;
        Path path = Paths.get(headerPath);
        return path.getParent() != null ? path.getParent().toString() : headerPath;
    }
}

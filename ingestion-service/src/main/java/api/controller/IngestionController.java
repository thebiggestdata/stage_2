package api.controller;

import api.dto.CrawlerStatusDto;
import api.dto.StorageResultDto;
import api.mapper.BookMapper;
import api.service.IngestionService;
import control.utils.CrawlerConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import model.StorageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ingestion-service")
@Tag(name = "Ingestion", description = "Book ingestion API for Project Gutenberg crawler")
public class IngestionController {
    private final IngestionService ingestionService;

    @Autowired
    public IngestionController(IngestionService ingestionService) {this.ingestionService = ingestionService;}

    @PostMapping("/download/{bookId}")
    @Operation(summary = "Download a specific book by ID", description = "Downloads and stores a book from Project Gutenberg by its ID")
    @ApiResponse(responseCode = "200", description = "Book downloaded successfully")
    @ApiResponse(responseCode = "500", description = "Failed to download book")
    public ResponseEntity<StorageResultDto> downloadBook(
            @Parameter(description = "Book ID to download from Project Gutenberg")
            @PathVariable int bookId) {
        try {
            StorageResult result = ingestionService.downloadBook(bookId);
            StorageResultDto dto = BookMapper.toDto(result);
            return result.success() ? ResponseEntity.ok(dto) : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/download/next")
    @Operation(summary = "Download the next book in sequence", description = "Downloads the next book based on the internal counter")
    @ApiResponse(responseCode = "200", description = "Next book downloaded successfully")
    @ApiResponse(responseCode = "500", description = "Failed to download next book")
    public ResponseEntity<StorageResultDto> downloadNextBook() {
        try {
            StorageResult result = ingestionService.downloadNextBook();
            StorageResultDto dto = BookMapper.toDto(result);
            return result.success() ? ResponseEntity.ok(dto) : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/crawl")
    @Operation(summary = "Crawl a range of books", description = "Downloads multiple books within the specified range")
    @ApiResponse(responseCode = "200", description = "Crawl completed successfully")
    @ApiResponse(responseCode = "500", description = "Crawl failed")
    public ResponseEntity<String> crawlRange(
            @Parameter(description = "First book ID to download")
            @RequestParam(defaultValue = "1") int startId,
            @Parameter(description = "Last book ID to download")
            @RequestParam(defaultValue = "100") int endId) {
        try {
            ingestionService.crawlRange(startId, endId);
            return ResponseEntity.ok(String.format("Crawl completed successfully for books %d to %d", startId, endId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Crawl failed: " + e.getMessage());
        }
    }

    @PutMapping("/config")
    @Operation(summary = "Update crawler configuration", description = "Updates the crawler settings including start/end IDs and delay")
    @ApiResponse(responseCode = "200", description = "Configuration updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid configuration")
    public ResponseEntity<String> updateConfig(
            @Parameter(description = "Start book ID")
            @RequestParam int startId,
            @Parameter(description = "End book ID")
            @RequestParam int endId,
            @Parameter(description = "Delay between downloads in milliseconds")
            @RequestParam long delay) {
        try {
            if (startId <= 0 || endId < startId || delay < 0) {
                return ResponseEntity.badRequest().body("Invalid configuration: check parameters");
            }
            CrawlerConfig newConfig = new CrawlerConfig(startId, endId, delay);
            ingestionService.updateConfiguration(newConfig);
            return ResponseEntity.ok(
                String.format("Configuration updated: startId=%d, endId=%d, delay=%dms", startId, endId, delay));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update configuration: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Get current crawler status", description = "Returns the current state of the crawler including current book ID")
    @ApiResponse(responseCode = "200", description = "Status retrieved successfully")
    public ResponseEntity<CrawlerStatusDto> getStatus() {
        CrawlerConfig config = ingestionService.getCurrentConfig();
        int currentId = ingestionService.getCurrentBookId();
        CrawlerStatusDto status = new CrawlerStatusDto(
            currentId,
            config.startId(),
            config.endId(),
            config.delay(),
            "RUNNING"
        );
        return ResponseEntity.ok(status);
    }
}

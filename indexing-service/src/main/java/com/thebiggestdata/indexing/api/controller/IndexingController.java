package com.thebiggestdata.indexing.api.controller;

import com.thebiggestdata.indexing.api.dto.IndexStatusDto;
import com.thebiggestdata.indexing.api.dto.IndexUpdateResultDto;
import com.thebiggestdata.indexing.api.dto.RebuildResultDto;
import com.thebiggestdata.indexing.api.service.IndexingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/indexing-service")
@Tag(name = "Indexing", description = "Book indexing and metadata extraction API")
public class IndexingController {

    private final IndexingService indexingService;

    @Autowired
    public IndexingController(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @PostMapping("/index/update/{bookId}")
    @Operation(
            summary = "Index a specific book",
            description = "Reads a book from the datalake, extracts metadata, and updates the inverted index"
    )
    @ApiResponse(responseCode = "200", description = "Book indexed successfully")
    @ApiResponse(responseCode = "500", description = "Failed to index book")
    public ResponseEntity<IndexUpdateResultDto> indexBook(
            @Parameter(description = "Book ID to index")
            @PathVariable int bookId,
            @Parameter(description = "Download date in YYYYMMDD format")
            @RequestParam String downloadDate,
            @Parameter(description = "Download hour in HH format")
            @RequestParam String downloadHour) {

        try {
            IndexUpdateResultDto result = indexingService.indexBook(bookId, downloadDate, downloadHour);

            if (result.success()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        } catch (Exception e) {
            IndexUpdateResultDto errorResult = new IndexUpdateResultDto(
                    bookId,
                    false,
                    "indexing_failed",
                    "Error: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    @PostMapping("/index/rebuild")
    @Operation(
            summary = "Rebuild entire index",
            description = "Rebuilds the entire inverted index and metadata from all books in the datalake"
    )
    @ApiResponse(responseCode = "200", description = "Index rebuilt successfully")
    @ApiResponse(responseCode = "500", description = "Rebuild failed")
    public ResponseEntity<RebuildResultDto> rebuildIndex() {
        try {
            RebuildResultDto result = indexingService.rebuildIndex();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            RebuildResultDto errorResult = new RebuildResultDto(
                    0,
                    0,
                    "0s",
                    "Rebuild failed: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    @GetMapping("/index/status")
    @Operation(
            summary = "Get indexing statistics",
            description = "Returns statistics about the current state of the inverted index and metadata storage"
    )
    @ApiResponse(responseCode = "200", description = "Status retrieved successfully")
    public ResponseEntity<IndexStatusDto> getStatus() {
        try {
            IndexStatusDto status = indexingService.getIndexStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
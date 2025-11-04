package com.thebiggestdata.ingestion.api.controller;

import com.thebiggestdata.ingestion.api.dto.BookListDto;
import com.thebiggestdata.ingestion.api.dto.BookStatusDto;
import com.thebiggestdata.ingestion.api.dto.IngestionResultDto;
import com.thebiggestdata.ingestion.api.service.IngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ingest")
@Tag(name = "Ingestion", description = "Book ingestion API for Project Gutenberg crawler")
public class IngestionController {
    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/{book_id}")
    @Operation(summary = "Ingest a book by ID", description = "Downloads a book from Project Gutenberg and stores it in the datalake")
    @ApiResponse(responseCode = "200", description = "Book processed")
    public ResponseEntity<IngestionResultDto> ingestBook(
            @Parameter(description = "Book ID from Project Gutenberg")
            @PathVariable("book_id") int bookId) {
        IngestionResultDto result = ingestionService.ingestBook(bookId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{book_id}")
    @Operation(summary = "Check book status", description = "Verifies if a book has been downloaded")
    @ApiResponse(responseCode = "200", description = "Status retrieved")
    public ResponseEntity<BookStatusDto> getBookStatus(
            @Parameter(description = "Book ID to check")
            @PathVariable("book_id") int bookId) {
        BookStatusDto status = ingestionService.getBookStatus(bookId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/list")
    @Operation(summary = "List downloaded books", description = "Returns all books in the datalake")
    @ApiResponse(responseCode = "200", description = "List retrieved")
    public ResponseEntity<BookListDto> listDownloadedBooks() {
        BookListDto bookList = ingestionService.listDownloadedBooks();
        return ResponseEntity.ok(bookList);
    }
}

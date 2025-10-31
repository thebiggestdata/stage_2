package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IngestionResultDto(
        @JsonProperty("book_id") int bookId,
        String status,
        String path
) {}

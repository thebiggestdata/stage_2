package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BookStatusDto(
        @JsonProperty("book_id") int bookId,
        String status
) {}

package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IndexUpdateResultDto(
        @JsonProperty("book_id")
        int bookId,

        @JsonProperty("success")
        boolean success,

        @JsonProperty("status")
        String status,

        @JsonProperty("message")
        String message
) {}
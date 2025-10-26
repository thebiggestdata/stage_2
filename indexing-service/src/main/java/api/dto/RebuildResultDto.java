package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RebuildResultDto(
        @JsonProperty("books_processed")
        int booksProcessed,

        @JsonProperty("books_failed")
        int booksFailed,

        @JsonProperty("elapsed_time")
        String elapsedTime,

        @JsonProperty("message")
        String message
) {}
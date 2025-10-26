package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BookDto (
        @JsonProperty("bookId")
        int bookId,
        @JsonProperty("header")
        String header,
        @JsonProperty("body")
        String body,
        @JsonProperty("footer")
        String footer
    )
{}

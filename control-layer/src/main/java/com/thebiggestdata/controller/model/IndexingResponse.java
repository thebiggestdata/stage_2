package com.thebiggestdata.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IndexingResponse(
        @JsonProperty("book_id") int bookId,
        boolean success,
        String status,
        String message
) {
    public boolean isSuccess() {
        return success;
    }
}
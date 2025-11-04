package com.thebiggestdata.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IngestionResponse(
        @JsonProperty("book_id") int bookId,
        String status,
        String path
) {
    public boolean isSuccess() {
        return "downloaded".equals(status) || "already_downloaded".equals(status);
    }

    public String getDate() {
        return java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")
        );
    }

    public String getHour() {
        return java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH")
        );
    }

    public String getTimestamp() {
        return getDate() + "/" + getHour();
    }
}
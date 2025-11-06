package com.thebiggestdata.application.dto;


public record BookIngestionResult(
        int bookId,
        boolean success,
        String message,
        String headerPath,
        String bodyPath,
        String timestamp
) {}

